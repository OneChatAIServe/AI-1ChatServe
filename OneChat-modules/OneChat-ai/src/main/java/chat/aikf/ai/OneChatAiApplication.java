package chat.aikf.ai;

import chat.aikf.common.core.constant.ServiceNameConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import chat.aikf.common.security.annotation.EnableCustomConfig;
import chat.aikf.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ai模块
 * 
 * @author 万洽ai客服
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class OneChatAiApplication
{
    public static void main(String[] args)
    {

        new SpringApplicationBuilder(OneChatAiApplication.class)
                .properties("spring.application.name=" + ServiceNameConstants.OneChatAi)
                .build()
                .run(args);

        System.out.println("(♥◠‿◠)ﾉﾞ  万洽AI客服ai模块启动成,官网:https://1chatserve.com   ლ(´ڡ`ლ)ﾞ");


    }
}

