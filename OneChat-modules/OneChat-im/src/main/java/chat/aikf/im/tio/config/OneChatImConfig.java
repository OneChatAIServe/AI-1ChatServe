package chat.aikf.im.tio.config;

import chat.aikf.common.core.config.OneChatProperties;
import chat.aikf.im.tio.handler.OneChatImMsgHandler;
import chat.aikf.im.tio.listener.OneChatImServerListener;
import chat.aikf.im.tio.starter.OneChatImStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OneChatImConfig {


    @Autowired
    private OneChatProperties oneChatProperties;


    @Autowired
    private TioClusterNodeProperties clusterNodeProperties;



    @Autowired
    private OneChatImMsgHandler msgHandler;

    @Autowired
    private OneChatImServerListener serverListener;


    /**
     * 初始化tio-webscoket
     * @return
     * @throws Exception
     */
    @Bean
    public OneChatImStarter oneChatImStarter() throws Exception {


        OneChatImStarter appStarter = new OneChatImStarter(clusterNodeProperties.getCurrentTioPort(), oneChatProperties.protocolName, oneChatProperties.getTimeOut(),msgHandler, serverListener);

        appStarter.getWsServerStarter().start();

        return appStarter;
    }



}