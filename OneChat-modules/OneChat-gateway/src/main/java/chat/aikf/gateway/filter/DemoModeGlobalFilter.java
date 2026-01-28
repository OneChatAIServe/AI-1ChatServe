package chat.aikf.gateway.filter;

import chat.aikf.gateway.config.properties.AppProperties;
import chat.aikf.gateway.config.properties.IgnoreWhiteProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Component
@Order(-100) // 优先级高，早于路由
public class DemoModeGlobalFilter implements GlobalFilter, Ordered {

    private final AppProperties appConfig;

    private final IgnoreWhiteProperties properties;

    public DemoModeGlobalFilter(AppProperties appConfig,IgnoreWhiteProperties properties) {
        this.appConfig = appConfig;
        this.properties=properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!appConfig.isDemoMode()) {
            return chain.filter(exchange); // 非演示环境，放行
        }

        HttpMethod method = exchange.getRequest().getMethod();
        String path = exchange.getRequest().getURI().getPath();



        if(properties.getWhites().contains(path)||appConfig.getIgnoreUrls().contains(path)){
            return chain.filter(exchange); // 白名单方向
        }

        // 只允许 GET、HEAD、OPTIONS（安全的只读方法）
        if (method == HttpMethod.GET ||
                method == HttpMethod.HEAD ||
                method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 拒绝所有写操作
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {"code":503,"message":"演示环境禁止编辑、新增、删除等操作"}
            """;
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -50; // 数值越小，优先级越高
    }

}