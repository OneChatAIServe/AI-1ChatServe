package chat.aikf.im.tio.handler.strategy;

import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.IdentityMsgDto;
import chat.aikf.im.tio.model.OneChatMsgDto;
import chat.aikf.im.tio.service.ChatMessageService;
import chat.aikf.im.tio.utils.PingUtils;
import chat.aikf.im.tio.utils.UrlUtils;
import chat.aikf.ops.api.constant.OneChatReadMsgState;
import chat.aikf.system.api.model.LoginUser;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;

import java.util.Date;
import java.util.Map;


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

        // 绑定用户
        Tio.bindUser(channelContext,  userAccount);

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
            if (StringUtils.isEmpty(text)) return null;

            //  第一步：优先判断是否为心跳消息
            if (PingUtils.isPingMessage(text)) {
                // 回复 pong，保持连接
                Tio.send(channelContext, WsResponse.fromText(PingUtils.buildPongMessageToUser(), Constants.UTF8));
                return null; // 不再处理后续逻辑
            }else{


                if (OneChatMsgDto.isValidOneChatMsgDto(text)) {
                    OneChatMsgDto chatMsgDto = JSONUtil.toBean(text, OneChatMsgDto.class);
                    chatMsgDto.setSendTime(new Date());
                    chatMsgDto.setClientType((String) channelContext.getAttribute(OneChatImConstant.CLIENT_TYPE));
                    chatMsgDto.setReadReceipt(OneChatReadMsgState.readReceipt); //客服发的消息标记为已读
                    chatMsgDto.setMsgSource(1);
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
