package chat.aikf.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 限流规则配置类
 */
@Configuration
public class KeyResolverConfiguration
{
    /**
     * ip限流
     * @return
     */
    @Bean
    public KeyResolver ipKeyResolver()
    {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
}
