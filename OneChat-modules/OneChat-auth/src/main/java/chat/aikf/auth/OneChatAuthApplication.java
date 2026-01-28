package chat.aikf.auth;

import chat.aikf.common.core.constant.ServiceNameConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import chat.aikf.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 认证授权中心
 * 
 * @author ruoyi
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class OneChatAuthApplication
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(OneChatAuthApplication.class)
                .properties("spring.application.name="+ ServiceNameConstants.OneChatAuth)
                .build().run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  万洽AI客服认证授权中心启动成功,官网:https://1chatserve.com   ლ(´ڡ`ლ)ﾞ");
    }
}
