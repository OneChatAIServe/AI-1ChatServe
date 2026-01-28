package chat.aikf.im.tio.service;

import chat.aikf.common.core.config.OneChatConfig;
import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.exception.ServiceException;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.*;
import chat.aikf.im.tio.starter.OneChatImStarter;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private OneChatConfig oneChatConfig;


    @Autowired
    private KfCacheRelUtils kfCacheRelUtils;







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
        if(OneChatImConstant.CLIENT_TYPE_GUEST.equals(chatMsgDto.getClientType())){ //发送给客服

            WsResponse response = WsResponse.fromText(JSONUtil.toJsonStr(UserIdentityMsgDto.builder().initState(2).visitorMsg(r.getData()).build()), Constants.UTF8);
            String toUserId = chatMsgDto.getToObj();
            Tio.sendToUser(oneChatImStarter.getServerTioConfig(),toUserId, response);
        }else{

            WsResponse response = WsResponse.fromText(JSONUtil.toJsonStr( UserIdentityMsgDto.builder().initState(chatMsgDto.getMsgStatus()==3?4:2).visitorMsg(r.getData()).build()), Constants.UTF8);
            String toUserId = new VisitorSessionKey(chatMsgDto.getToObj(), chatMsgDto.getWebStyleId().toString()).toString();
            Tio.sendToUser(oneChatImStarter.getServerTioConfig(),toUserId, response);
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


            //通知访客初始化状态(以及提示语)
            WsResponse responseToVisitor = WsResponse.fromText(JSONUtil.toJsonStr(GuestIdentityMsgDto.buildObj(msgDto, visitor.getVisitorMsgs().stream().findAny().get(),r.getData(),oneChatConfig.sessionTime)), Constants.UTF8);
            Tio.sendToUser(oneChatImStarter.getServerTioConfig(),new VisitorSessionKey(visitor.getVisitorId(),visitor.getWebStyleId().toString()).toString(),responseToVisitor);


            if(msgDto.getReceptionState() != OneChatVisitorSate.END_STATE){ //离线不做通知与缓存
                //给员工通知消息
                WsResponse responseToUser = WsResponse.fromText(JSONUtil.toJsonStr(UserIdentityMsgDto.builder().initState(
                        msgDto.getReceptionState()
                ).build()), Constants.UTF8);
                Tio.sendToUser(oneChatImStarter.getServerTioConfig(), msgDto.getToObj(),responseToUser);


                //构建访客连接初始化数据
                kfCacheRelUtils.linkInitCache(GuestIdentityMsgDto.buildObj(msgDto, visitor.getVisitorMsgs().stream().findAny().get(),r.getData(),oneChatConfig.sessionTime),visitor.getWebStyleId().toString());
            }



        }

    }
}