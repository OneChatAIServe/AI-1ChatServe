package chat.aikf.im.tio.config;

import chat.aikf.common.core.constant.ServiceNameConstants;
import chat.aikf.common.core.utils.StringUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * tio作为新的服务注册到nacos中
 */
@Component
@Slf4j
public class TioNacosRegisterConfig {

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;

    @Value("${spring.cloud.nacos.discovery.username:}")
    private String username;

    @Value("${spring.cloud.nacos.discovery.password:}")
    private String password;

    @Autowired
    private TioClusterNodeProperties tioClusterNodeProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void registerTioInstance() throws NacosException {
        // 创建NamingService实例
        NamingService namingService = createNamingService();


        // 🔍 节点唯一性校验
        validateNodeUniqueness(namingService);

        // 创建并配置实例
        Instance instance = createInstance();

        // 注册实例到Nacos
        namingService.registerInstance(ServiceNameConstants.OneChatImTio, instance);

        log.info("🎯 TIO实例注册到Nacos成功。节点标识: [{}], Tio端口: [{}], Web端口: [{}]",
                tioClusterNodeProperties.getCurrentNodeId(),
                tioClusterNodeProperties.getCurrentTioPort(),
                tioClusterNodeProperties.getCurrentServerPort());
    }

    /**
     * 🔍 节点唯一性校验方法
     */
    private void validateNodeUniqueness(NamingService namingService) throws NacosException {
        String currentNodeId = tioClusterNodeProperties.getCurrentNodeId();

        List<Instance> existingInstances = namingService.getAllInstances(
                ServiceNameConstants.OneChatImTio, true
        );

        for (Instance instance : existingInstances) {
            String existingNodeId = instance.getMetadata().get("node.id");
            if (currentNodeId.equals(existingNodeId)) {
                String errorMessage = String.format(
                        "🚫 启动失败！节点ID '%s' 已存在健康实例（IP=%s, Port=%s）。请勿重复启动。",
                        currentNodeId, instance.getIp(), instance.getPort()
                );
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        log.info("✅ 节点唯一性校验通过，节点ID: {}", currentNodeId);
    }

    /**
     * 创建Instance实例 - 使用新的配置获取方式
     */
    private Instance createInstance() {
        String ip = getLocalIp();
        String nodeId = tioClusterNodeProperties.getCurrentNodeId();
        Integer tioPort = tioClusterNodeProperties.getCurrentTioPort();

        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(tioPort); // 使用动态获取的Tio端口
        instance.setServiceName(ServiceNameConstants.OneChatImTio);
        instance.setWeight(1.0);
        instance.setEnabled(true);
        instance.setHealthy(true);

        // 设置元数据
        Map<String, String> metadata = new HashMap<>();
        metadata.put("protocol", "websocket");
        metadata.put("node.identifier", nodeId);
        metadata.put("node.id", nodeId);
        metadata.put("web.port", String.valueOf(tioClusterNodeProperties.getCurrentServerPort()));
        metadata.put("tio.port", String.valueOf(tioPort));

        instance.setMetadata(metadata);

        return instance;
    }

    // createNamingService() 和 getLocalIp() 方法保持不变
    private NamingService createNamingService() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", nacosServerAddr);
        if (!namespace.isEmpty()) {
            properties.setProperty("namespace", namespace);
        }
        if (!username.isEmpty()) {
            properties.setProperty("username", username);
            properties.setProperty("password", password);
        }
        return NamingFactory.createNamingService(properties);
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("获取本机IP失败，使用回退地址: 127.0.0.1", e);
            return "127.0.0.1";
        }
    }
}