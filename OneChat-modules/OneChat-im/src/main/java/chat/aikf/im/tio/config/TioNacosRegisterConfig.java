package chat.aikf.im.tio.config;

import chat.aikf.common.core.config.OneChatConfig;
import chat.aikf.common.core.constant.ServiceNameConstants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Properties;


/**
 * tio作为新的服务注册到nacos中
 */
@Component
@Slf4j
public class TioNacosRegisterConfig {



    @Autowired
    private OneChatConfig oneChatConfig;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace; // 注意：默认空字符串，不是 "public"

    @Value("${spring.cloud.nacos.discovery.username:}")
    private String username;

    @Value("${spring.cloud.nacos.discovery.password:}")
    private String password;

    @EventListener(ApplicationReadyEvent.class)
    public void registerTioInstance() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", nacosServerAddr);
        if (!namespace.isEmpty()) {
            properties.setProperty("namespace", namespace);
        }
        if (!username.isEmpty()) {
            properties.setProperty("username", username);
            properties.setProperty("password", password);
        }

        NamingService namingService = NamingFactory.createNamingService(properties);

        String ip = getLocalIp();
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(oneChatConfig.serverPort);
        instance.setServiceName(ServiceNameConstants.OneChatImTio);
        instance.setWeight(1.0);
        instance.setEnabled(true);
        instance.setHealthy(true);
        instance.setMetadata(java.util.Map.of("protocol", "websocket"));

        namingService.registerInstance(ServiceNameConstants.OneChatImTio, instance);

        log.info("tio注册nacos成功");
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {

            return "127.0.0.1";
        }
    }
}