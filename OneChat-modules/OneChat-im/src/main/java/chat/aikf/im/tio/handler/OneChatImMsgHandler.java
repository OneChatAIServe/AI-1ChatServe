package chat.aikf.im.tio.handler;




import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.handler.strategy.ClientStrategyFactory;
import chat.aikf.im.tio.model.GuestIdentityMsgDto;
import chat.aikf.im.tio.model.OneChatMsgDto;
import chat.aikf.im.tio.service.ChatMessageService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author tanyaowu
 * 2017年6月28日 下午5:32:38
 */
@Component
@Slf4j
public class OneChatImMsgHandler implements IWsMsgHandler {

    @Autowired
    private ClientStrategyFactory clientStrategyFactory;


    @Autowired
    private ChatMessageService chatMessageService;


    @Autowired
    private MqSender mqSender;





    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String path = request.getRequestLine().getPath();

        if (path.startsWith("/"+OneChatImConstant.CLIENT_TYPE_GUEST)) {
            return clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_GUEST).handshake(request, httpResponse, channelContext);
        } else if (path.startsWith("/"+OneChatImConstant.CLIENT_TYPE_USER)) {
            return  clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_USER).handshake(request, httpResponse, channelContext);
        } else {
            log.warn("不支持的路径: {}", path);
            return null;
        }
    }



    /**
     * 握手后初始化操作
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     * @throws Exception
     * @author tanyaowu
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

        // 可在此处触发事件，如分配客服、发送欢迎语等
        String clientType = (String) channelContext.getAttribute(OneChatImConstant.CLIENT_TYPE);
        if (OneChatImConstant.CLIENT_TYPE_GUEST.equals(clientType)) { //客户端操作逻辑
            clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_GUEST).onAfterHandshaked(httpRequest,httpResponse,channelContext);
        }else if(OneChatImConstant.CLIENT_TYPE_USER.equals(clientType)){ //管理端逻辑
            clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_USER).onAfterHandshaked(httpRequest,httpResponse,channelContext);
        }
    }

    /**
     * 字节消息（binaryType = arraybuffer）
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) {

        return null;
    }

    /**
     *
     * 当客户端发close flag时，会走这个方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        //关闭通道
        Tio.remove(channelContext, "receive close flag");
        //从指定的队列中移除相关的映射
        return null;
    }

    /*
     * 字符消息（binaryType = blob）过来后会走这个方法
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext){
        String clientType = (String) channelContext.getAttribute(OneChatImConstant.CLIENT_TYPE);
        if (OneChatImConstant.CLIENT_TYPE_GUEST.equals(clientType)) { //客户端操作逻辑
            clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_GUEST).onText(wsRequest,text,channelContext);
        }else if(OneChatImConstant.CLIENT_TYPE_USER.equals(clientType)){ //管理端逻辑
            clientStrategyFactory.getStrategy(OneChatImConstant.CLIENT_TYPE_USER).onText(wsRequest,text,channelContext);
        }

        return null;
    }




}
