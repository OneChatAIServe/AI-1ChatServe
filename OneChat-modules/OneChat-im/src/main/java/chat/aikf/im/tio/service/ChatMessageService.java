package chat.aikf.im.tio.service;

import chat.aikf.common.core.config.OneChatProperties;
import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.exception.ServiceException;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.*;
import chat.aikf.im.tio.starter.OneChatImStarter;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.im.tio.utils.OneChatTioUtils;
import chat.aikf.im.tio.utils.PingUtils;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.client.ClientChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsResponse;
import java.util.*;


@Service
@Slf4j
public class ChatMessageService {



    @Autowired
    private RemoteKfVisitorService remoteKfVisitorService;


    @Autowired
    private OneChatImStarter oneChatImStarter;



    @Autowired
    private OneChatProperties oneChatProperties;


    @Autowired
    private KfCacheRelUtils kfCacheRelUtils;

    @Autowired
    private MqSender mqSender;







    //访客消息入库，更新。并提示通知访客
    public void savePendingMessage(OneChatMsgDto chatMsgDto){
        OneChatKfVisitorMsg visitorMsg = OneChatKfVisitorMsg.builder()
                .kfVisitorId(chatMsgDto.getKfVisitorId().toString())
                .fromObj(chatMsgDto.getFromObj())
                .showAvatar(chatMsgDto.getShowAvatar())
                .showName(chatMsgDto.getShowName())
                .toObj(chatMsgDto.getToObj())
                .kfRuleId(chatMsgDto.getKfRuleId())
                .msgType(chatMsgDto.getMsgType())
                .content(chatMsgDto.getContent())
                .msgSource(chatMsgDto.getMsgSource())
                .readReceipt(chatMsgDto.getReadReceipt())
                .sendTime(new Date())
                .build();

        R<OneChatKfVisitorMsg> r = remoteKfVisitorService.addMsgVisitor(visitorMsg, SecurityConstants.INNER);

        if (R.FAIL == r.getCode()) {
            log.error("会话消息入库失败:"+r.getMsg());
            throw new ServiceException(r.getMsg());
        }

        chatMsgDto.setMsgId(r.getData().getId().toString());
        chatMsgDto.setSendTime(visitorMsg.getSendTime());


        try {

            //广播通知tio集群节点发送消息
            mqSender.broadcastMessage(BroadcastMsgDto.builder()
                    .oneChatKfVisitorMsg(r.getData())
                    .sendType(OneChatImConstant.SEND_MSG)
                    .chatMsgDto(chatMsgDto)
                    .build());

        }catch (Exception e){
            log.warn("MQ 发送失败，chatMsgDto={}，降级落库", JSONUtil.toJsonStr(chatMsgDto));
            tioColonySendMessage(BroadcastMsgDto.builder()
                    .oneChatKfVisitorMsg(r.getData())
                    .chatMsgDto(chatMsgDto)
                    .build());

        }








    }


    /**
     * tio发送消息给客户
     * @param broadcastMsgDto
     */
    public void tioColonySendMessage(BroadcastMsgDto broadcastMsgDto){
        BroadcastMsgDto.restartToUserId(broadcastMsgDto);
        if(OneChatTioUtils.localUserIdExist(oneChatImStarter.getServerTioConfig(), broadcastMsgDto.getToUserId())){//判断消息发送目标是否存在本机中

            String sendType = broadcastMsgDto.getSendType();

            if(OneChatImConstant.GUEST_TIO_PING.equals(sendType)){
                    Tio.sendToUser(oneChatImStarter.getServerTioConfig(),broadcastMsgDto.getToUserId(), WsResponse.fromText(PingUtils.buildPongMessageToGuest(), Constants.UTF8));

            }else if(OneChatImConstant.USER_TIO_PING.equals(sendType)){
                    Tio.sendToUser(oneChatImStarter.getServerTioConfig(),broadcastMsgDto.getToUserId(), WsResponse.fromText(PingUtils.buildPongMessageToUser(), Constants.UTF8));

            }else if(OneChatImConstant.NOTIFY_USER.equals(sendType)){//访客接入，初始化，同时管理端刷新对话中列表通知

                //给员工通知消息
                WsResponse responseToUser = WsResponse.fromText(JSONUtil.toJsonStr(UserIdentityMsgDto.builder().initState(
                        broadcastMsgDto.getMsgDto().getReceptionState()
                ).build()), Constants.UTF8);
                Tio.sendToUser(oneChatImStarter.getServerTioConfig(), broadcastMsgDto.getMsgDto().getToObj(),responseToUser);

            }else if(OneChatImConstant.SESSION_RESP.equals(sendType)){

                WsResponse responseToVisitor = WsResponse.fromText(JSONUtil.toJsonStr(broadcastMsgDto.getIdentityMsgDto()), Constants.UTF8);
                Tio.sendToUser(oneChatImStarter.getServerTioConfig(),broadcastMsgDto.getToUserId(),responseToVisitor);

            }else if(OneChatImConstant.SEND_MSG.equals(sendType)){

                OneChatMsgDto chatMsgDto = broadcastMsgDto.getChatMsgDto();
                OneChatKfVisitorMsg oneChatKfVisitorMsg = broadcastMsgDto.getOneChatKfVisitorMsg();

                if(null != chatMsgDto && null !=oneChatKfVisitorMsg){
                    if(OneChatImConstant.CLIENT_TYPE_GUEST.equals(chatMsgDto.getClientType())){ //发送给客服
                        WsResponse response = WsResponse.fromText(JSONUtil.toJsonStr(UserIdentityMsgDto.builder().initState(2).visitorMsg(oneChatKfVisitorMsg).build()), Constants.UTF8);
                        String toUserId = chatMsgDto.getToObj();
                        Tio.sendToUser(oneChatImStarter.getServerTioConfig(),toUserId, response);
                    }else{ //发送给访客
                        WsResponse response = WsResponse.fromText(JSONUtil.toJsonStr( UserIdentityMsgDto.builder().initState(chatMsgDto.getMsgStatus()==3?4:2).visitorMsg(oneChatKfVisitorMsg).build()), Constants.UTF8);
                        String toUserId = new VisitorSessionKey(chatMsgDto.getToObj(), chatMsgDto.getWebStyleId().toString()).toString();
                        Tio.sendToUser(oneChatImStarter.getServerTioConfig(),toUserId, response);
                    }
                }
            }

        }

    }


    //处理访客信息(主动接入)
    public  void handleVisitorInfo(IdentityMsgDto msgDto){
        OneChatkfVisitor visitor = msgDto.getVisitor();
        if(null != visitor){
            //访客消息入库
            R<OneChatkfVisitor> r = remoteKfVisitorService.
                    addOrUpdate(visitor, SecurityConstants.INNER);
            if (R.FAIL == r.getCode()) {
                log.error("更新访客信息失败:"+r.getMsg());
                Tio.closeUser(oneChatImStarter.getServerTioConfig(),new VisitorSessionKey(visitor.getVisitorId(),visitor.getKfRuleId().toString()).toString(),"更新访客信息失败,请稍后重试");
                throw new ServiceException(r.getMsg());
            }
            if(null != r.getData().getId()){
                msgDto.setKfVisitorId(r.getData().getId().toString());
            }


            //构建tio链接
            Tio.bindUser( new ClientChannelContext(oneChatImStarter.getServerTioConfig()),  new VisitorSessionKey(visitor.getVisitorId(),visitor.getWebStyleId().toString()).toString());
            log.info("访客端连接成功，channelId: {}", new VisitorSessionKey(visitor.getVisitorId(),visitor.getWebStyleId().toString()).toString());

            //通知访客初始化状态(以及提示语)
            WsResponse responseToVisitor = WsResponse.fromText(JSONUtil.toJsonStr(GuestIdentityMsgDto.buildObj(msgDto, visitor.getVisitorMsgs().stream().findAny().get(),r.getData(), oneChatProperties.sessionTime)), Constants.UTF8);
            Tio.sendToUser(oneChatImStarter.getServerTioConfig(),new VisitorSessionKey(visitor.getVisitorId(),visitor.getWebStyleId().toString()).toString(),responseToVisitor);


            if(msgDto.getReceptionState() != OneChatVisitorSate.END_STATE){ //离线不做通知与缓存
                //构建访客连接初始化数据
                kfCacheRelUtils.linkInitGuestCache(GuestIdentityMsgDto.buildObj(msgDto, visitor.getVisitorMsgs().stream().findAny().get(),r.getData(), oneChatProperties.sessionTime),visitor.getWebStyleId().toString());

                //通知员工端更新列表
                mqSender.broadcastMessage(BroadcastMsgDto.builder()
                        .sendType(OneChatImConstant.NOTIFY_USER)
                        .msgDto(msgDto)
                        .build());
            }



        }

    }
}