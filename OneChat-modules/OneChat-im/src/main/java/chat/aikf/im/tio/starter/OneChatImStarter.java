package chat.aikf.im.tio.starter;

import chat.aikf.im.tio.handler.OneChatImMsgHandler;
import lombok.Data;
import org.tio.server.TioServerConfig;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.WsTioServerListener;


/**
 * tio初始化类
 */
@Data
public class OneChatImStarter {

    private WsServerStarter wsServerStarter;

    private TioServerConfig serverTioConfig;

    /**
     *
     * @author tanyaowu
     */
    public OneChatImStarter(int port,String protocolName,int timeOut, OneChatImMsgHandler wsMsgHandler, WsTioServerListener serverListener) throws Exception {
        wsServerStarter = new WsServerStarter(port, wsMsgHandler);

        serverTioConfig = wsServerStarter.getTioServerConfig();

        serverTioConfig.setName(protocolName);
        serverTioConfig.setTioServerListener(serverListener);
        //设置心跳超时时间
        serverTioConfig.setHeartbeatTimeout(timeOut);



    }



}
