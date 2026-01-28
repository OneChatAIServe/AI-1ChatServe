package chat.aikf.file;

import chat.aikf.common.core.constant.ServiceNameConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 文件服务
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class OneChatFileApplication
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(OneChatFileApplication.class)
                .properties("spring.application.name="+ ServiceNameConstants.OneChatFile)
                .build().run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  万洽AI客服文件模块启动成功,官网:https://1chatserve.com   ლ(´ڡ`ლ)ﾞ");
    }
}
