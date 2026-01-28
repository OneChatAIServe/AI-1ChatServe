package chat.aikf.system;

import chat.aikf.common.core.constant.ServiceNameConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import chat.aikf.common.security.annotation.EnableCustomConfig;
import chat.aikf.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 系统模块
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class OneChatSystemApplication
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(OneChatSystemApplication.class)
                .properties("spring.application.name=" + ServiceNameConstants.OneChatSystem)
                .build()
                .run(args);

        System.out.println("(♥◠‿◠)ﾉﾞ  万洽AI客服系统模块启动成,官网:https://1chatserve.com   ლ(´ڡ`ლ)ﾞ");


    }
}

