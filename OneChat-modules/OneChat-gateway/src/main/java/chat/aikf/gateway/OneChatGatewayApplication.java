package chat.aikf.gateway;

import chat.aikf.common.core.constant.ServiceNameConstants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;


/**
 * 网关启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class OneChatGatewayApplication
{
        public static void main(String[] args) {

                new SpringApplicationBuilder(OneChatGatewayApplication.class)
                        .properties("spring.application.name=" + ServiceNameConstants.OneChatGateWay)
                        .build().run(args);
                System.out.println("(♥◠‿◠)ﾉﾞ 万洽AI客服网关启动成功...");
        }
}
