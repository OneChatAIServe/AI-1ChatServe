package chat.aikf.im.tio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "one-chat.tio.node")
public class TioClusterNodeProperties {

    // 默认节点标识
    private String currentNode = "im-node-bj-01";

    // 节点配置映射
    private Map<String, NodeConfig> config;

    @Data
    public static class NodeConfig {
        private Server server;
        private TioConfig tio;
    }

    @Data
    public static class Server {
        private Integer port;
    }

    @Data
    public static class TioConfig {
        private Integer serverPort;
        private String currentNodeId;
    }

    /**
     * 获取当前激活节点的配置
     */
    public NodeConfig getCurrentNodeConfig() {
        if (config == null || !config.containsKey(currentNode)) {
            throw new IllegalStateException("未找到当前节点 '" + currentNode + "' 的配置");
        }
        return config.get(currentNode);
    }

    /**
     * 获取当前节点的Web端口（server.port）
     */
    public Integer getCurrentServerPort() {
        NodeConfig nodeConfig = getCurrentNodeConfig();
        return nodeConfig.getServer() != null ? nodeConfig.getServer().getPort() : null;
    }

    /**
     * 获取当前节点的Tio端口（tio.server-port）
     */
    public Integer getCurrentTioPort() {
        NodeConfig nodeConfig = getCurrentNodeConfig();
        return nodeConfig.getTio() != null ? nodeConfig.getTio().getServerPort() : null;
    }

    /**
     * 获取当前节点的标识
     */
    public String getCurrentNodeId() {
        NodeConfig nodeConfig = getCurrentNodeConfig();
        return nodeConfig.getTio() != null ? nodeConfig.getTio().getCurrentNodeId() : null;
    }

    /**
     * 获取指定节点的配置
     */
    public NodeConfig getNodeConfig(String nodeIdentity) {
        if (config == null || !config.containsKey(nodeIdentity)) {
            throw new IllegalStateException("未找到节点 '" + nodeIdentity + "' 的配置");
        }
        return config.get(nodeIdentity);
    }

    /**
     * 获取所有可用的节点标识
     */
    public Set<String> getAvailableNodeIdentities() {
        return config != null ? config.keySet() : Collections.emptySet();
    }
}