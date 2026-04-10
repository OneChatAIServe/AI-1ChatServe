package chat.aikf.im.tio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * 自定义web服务端口
 */
@Component
public class ServerPortConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {


    @Autowired
    private TioClusterNodeProperties tioClusterNodeProperties;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        // 在此设置您想要的端口号
        factory.setPort(tioClusterNodeProperties.getCurrentServerPort());

    }
}