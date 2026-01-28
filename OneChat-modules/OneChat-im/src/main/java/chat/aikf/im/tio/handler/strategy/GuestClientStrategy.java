package chat.aikf.im.tio.handler.strategy;

import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.constant.OneChatCacheKeyConstants;
import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.conversation.service.VisitorStateService;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.GuestIdentityMsgDto;
import chat.aikf.im.tio.model.OneChatMsgDto;
import chat.aikf.im.tio.model.VisitorSessionKey;
import chat.aikf.im.tio.service.ChatMessageService;
import chat.aikf.im.tio.utils.PingUtils;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import java.util.Date;


/**
 * 客户端握手与发送消息处理逻辑
 */
@Slf4j
public class GuestClientStrategy implements ClientStrategy{
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

        log.info("真实ip:"+request.getHeaders());
        String visitorId = request.getHeader(SecurityConstants.VISITOR_ID);
        if (StringUtils.isEmpty(visitorId)) {
            log.warn("访客端缺少 访客id");
            return null;
        }

        String webStyleId = request.getHeader(SecurityConstants.KF_WEB_STYLE_ID);
        if (StringUtils.isEmpty(webStyleId)) {
            log.warn("访客端缺少 样式id");
            return null;
        }

        // 绑定用户
        Tio.bindUser(channelContext,  new VisitorSessionKey(visitorId, webStyleId).toString());
        log.info("访客端连接成功，channelId: {}",  new VisitorSessionKey(visitorId, webStyleId));

        // 保存客户端类型
        channelContext.setAttribute(OneChatImConstant.CLIENT_TYPE, OneChatImConstant.CLIENT_TYPE_GUEST);



        log.info("WS 握手成功 visitorKey={}", new VisitorSessionKey(visitorId, webStyleId));






        return httpResponse;



    }




    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {


        //1. 获取当前访客的真实身份（来自 handshake 中 bindUser 的值）
        VisitorSessionKey restored = VisitorSessionKey.fromString(channelContext.userid);
        if (restored == null) {
            log.warn("握手后未绑定有效访客身份，关闭连接");


            Tio.close(channelContext, "身份异常");


            return;
       }


        String initKey =OneChatCacheKeyConstants.ImKeyGenerator.getInitVisitorSessionKey(restored.visitorId(),restored.webStyleId());
        try {

            GuestIdentityMsgDto msgDto = SpringUtils.getBean(RedisService.class).getCacheObject(initKey);

            //已初始化 → 从缓存中返回初始化的数据
            if(null != msgDto){
                WsResponse responseToVisitor = WsResponse.fromText(JSONUtil.toJsonStr(msgDto), Constants.UTF8);
                Tio.send(channelContext,responseToVisitor);
            }else{
                //接入中
                SpringUtils.getBean(VisitorStateService.class).processByStateToGuest(httpRequest,restored, OneChatVisitorSate.IDLE_STATE);
            }

        } catch (Exception e) {
            log.error("发送身份通知失败", e);
            //出现异常删除初始化锁
            SpringUtils.getBean(RedisService.class).deleteObject(initKey);
            Tio.close(channelContext, "初始化失败");
        }














    }

    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) {
        try {
            if (StringUtils.isEmpty(text)) return null;

            // 👇 第一步：优先判断是否为心跳消息
            if (PingUtils.isPingMessage(text)) {
                // 回复 pong，保持连接
                Tio.send(channelContext, WsResponse.fromText(PingUtils.buildPongMessageToGuest(), Constants.UTF8));
                return null; // 不再处理后续逻辑
            }else{


                if (OneChatMsgDto.isValidOneChatMsgDto(text)) {
                    OneChatMsgDto chatMsgDto = JSONUtil.toBean(text, OneChatMsgDto.class);
                    chatMsgDto.setSendTime(new Date());
                    chatMsgDto.setClientType((String) channelContext.getAttribute(OneChatImConstant.CLIENT_TYPE));
                    chatMsgDto.setMsgSource(0);
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
            log.error("处理 C端 消息异常: {}", text, e);
        }
        return null;
    }


}
