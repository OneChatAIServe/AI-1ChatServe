package chat.aikf.im.tio.handler.strategy;

import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.config.TioClusterNodeProperties;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.BroadcastMsgDto;
import chat.aikf.im.tio.model.OneChatMsgDto;
import chat.aikf.im.tio.service.ChatMessageService;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.im.tio.utils.PingUtils;
import chat.aikf.im.tio.utils.UrlUtils;
import chat.aikf.ops.api.constant.OneChatReadMsgState;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.lock.SetWithLock;
import org.tio.websocket.common.WsRequest;

import java.util.Date;


/**
 * 员工管理端
 */
@Slf4j
public class UserClientStrategy implements ClientStrategy{
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

        String queryString = request.getRequestLine().queryString;
        if(StringUtils.isEmpty(queryString)){
            // 拒绝连接
            Tio.close(channelContext, "缺少用户参数");
            return null;
        }

        String userAccount = UrlUtils.parseQueryString(queryString).get("userAccount");
        if(StringUtils.isEmpty(userAccount)){
            // 拒绝连接
            Tio.close(channelContext, "缺少员工账号");
            return null;
        }

        RLock lock = SpringUtils.getBean(RedisService.class).getLock(userAccount);
        if(lock.tryLock()){

            try {
                String linkInitUserCache
                        = SpringUtils.getBean(KfCacheRelUtils.class).findLinkInitUserCache(userAccount);

                if(StringUtils.isEmpty(linkInitUserCache)){//存在则创建
                    // 绑定用户
                    Tio.bindUser(channelContext,  userAccount);
                    //绑定登陆账号与节点
                    SpringUtils.getBean(KfCacheRelUtils.class).linkInitUserCache(userAccount);
                }else{
                    //是否在当前节点中
                    if(SpringUtils.getBean(TioClusterNodeProperties.class)
                            .getCurrentNode().equals(linkInitUserCache)){//存在当前节点，则判断当前tio是否链接，如果没有链接则连接(防止一些突发情况，导致的tio链接断开，但是缓存中的初始化数据还未过期，导致的问题)
                        SetWithLock<ChannelContext> userChannels = Tio.getByUserid(channelContext.getTioConfig(), userAccount);
                        if (userChannels == null || userChannels.getObj().isEmpty()) {
                            // 绑定用户
                            Tio.bindUser(channelContext,  userAccount);
                            //绑定登陆账号与节点
                            SpringUtils.getBean(KfCacheRelUtils.class).linkInitUserCache(userAccount);
                        }
                    }
                }
            }catch (Exception e){
                log.error("客服链接失败:"+e);
            }finally {
                lock.unlock();
            }

        }



        // 保存客户端类型
        channelContext.setAttribute(OneChatImConstant.CLIENT_TYPE, OneChatImConstant.CLIENT_TYPE_USER);

        return httpResponse;
    }

    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {








    }

    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) {
        try {
            if (StringUtils.isEmpty(text)) {
                return null;
            }

            //  第一步：优先判断是否为心跳消息
            if (PingUtils.isPingMessage(text)) {
                // 回复 pong，保持连接
                SpringUtils.getBean(MqSender.class).broadcastMessage(BroadcastMsgDto.builder()
                        .sendType(OneChatImConstant.USER_TIO_PING)
                        .toUserId(channelContext.userid)
                        .build());
                return null; // 不再处理后续逻辑
            }else{


                if (OneChatMsgDto.isValidOneChatMsgDto(text)) {
                    OneChatMsgDto chatMsgDto = JSONUtil.toBean(text, OneChatMsgDto.class);
                    chatMsgDto.setSendTime(new Date());
                    chatMsgDto.setClientType((String) channelContext.getAttribute(OneChatImConstant.CLIENT_TYPE));
                    chatMsgDto.setReadReceipt(OneChatReadMsgState.readReceipt); //客服发的消息标记为已读
                    chatMsgDto.setMsgSource(OneChatImConstant.USER_MSGSOURCE);
                try {
                    // 尝试发 MQ
                  SpringUtils.getBean(MqSender.class).sendMsg(CommonMqConstants.CHAT_MESSAGE_PRODUCER,chatMsgDto);
                } catch (Exception mqEx) {
                    log.warn("MQ 发送失败，chatMsgDto={}，降级落库", JSONUtil.toJsonStr(chatMsgDto), mqEx);
                    // 降级：直接保存到 DB
                    SpringUtils.getBean(ChatMessageService.class).savePendingMessage(chatMsgDto);
                }
                }
            }


        } catch (Exception e) {
            log.error("处理 B端 消息异常: {}", text, e);
        }
        return null;
    }


}
