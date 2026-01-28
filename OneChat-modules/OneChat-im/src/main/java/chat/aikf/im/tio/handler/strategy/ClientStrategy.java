package chat.aikf.im.tio.handler.strategy;


import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;

/**
 * 行为策略接口
 */
public interface ClientStrategy {
    /**
     * 执行握手逻辑（绑定用户、校验身份等）
     * @param request HTTP 请求
     * @param httpResponse 响应
     * @param channelContext 通道上下文
     * @return 是否允许握手继续
     */
    HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception;


    /**
     * 握手成功后执行操作
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     */
    void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext);


    /**
     * 聊天消息处理
     * @param wsRequest
     * @param text
     * @param channelContext
     * @return
     */
    Object onText(WsRequest wsRequest, String text, ChannelContext channelContext);

}
