package chat.aikf.common.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "one-chat.tio")
public class OneChatConfig {

    /**
     * 协议名字(可以随便取，主要用于开发人员辨识)
     */
    public String protocolName= "showcase";

    /**
     * 监听端口
     */
    public  int serverPort = 9326;

    /**
     * 心跳超时时间，单位：毫秒
     */
    public  int timeOut = 60000;


    /**
     * 会话时长:分钟。默认2小时，超过2小时则过期
     */
    public Integer sessionTime=120;


}