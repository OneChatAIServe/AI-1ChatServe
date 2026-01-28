package chat.aikf.gateway.filter;



import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.constant.TokenConstants;
import cn.hutool.core.collection.CollectionUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 匿名访客 WebSocket 会话认证过滤器
 * 验证短期会话 Token（由 /api/guest/session 接口签发）
 */
@Component
public class GuestTokenAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GuestTokenAuthFilter.class);

    /**
     * 访客短期token密钥
     */
    @Value("${one-chat.auth.visitor-session.secret}")
    private String visitorSessionSecret;




    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 另一种写法：如果不是任何受保护路径，就放行
        if (!path.startsWith("/ws/guest")
                && !path.startsWith("/file/chatUploadToVisitor")) {
            return chain.filter(exchange);
        }




        // 1. 从查询参数获取短期会话 Token
        String sessionToken = null;

        if (path.startsWith("/ws/guest")){
            sessionToken= request.getQueryParams().getFirst(SecurityConstants.SESSION_TOKEN_PARAM); //路径获取
        }else if(path.startsWith("/file/chatUploadToVisitor")){
            sessionToken= getToken(request); //请求头获取
        }
//        else if(path.startsWith("/file/chatMsgFile")&&CollectionUtil.isNotEmpty(request.getHeaders().get("client_type"))){
//            sessionToken= getToken(request); //请求头获取
//        }
        if (StringUtils.isEmpty(sessionToken)) {
            return unauthorizedResponse(exchange, "缺少会话凭证（token）");
        }

        try {
            // 2. 解析短期 Token
            Claims claims = Jwts.parser()
                    .setSigningKey(visitorSessionSecret)
                    .parseClaimsJws(sessionToken)
                    .getBody();

            String visitorId = claims.getSubject();

            if (StringUtils.isEmpty(visitorId)) {
                return unauthorizedResponse(exchange, "会话凭证缺少访客身份");
            }


            String kfRuleId = claims.get(SecurityConstants.KF_WEB_STYLE_ID,String.class);

            if (StringUtils.isEmpty(kfRuleId)) {
                return unauthorizedResponse(exchange, "缺少客服规则id");
            }

            // 3. 透传关键信息给后端
            ServerHttpRequest mutated = request.mutate()
                    .header(SecurityConstants.VISITOR_ID, visitorId)
                    .header(SecurityConstants.KF_WEB_STYLE_ID,kfRuleId)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (Exception e) {
            log.warn("会话凭证验证失败: {}", e.getMessage());
            return unauthorizedResponse(exchange, "无效或已过期的会话凭证");
        }
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request)
    {
        String token = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_C_HEADER);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (chat.aikf.common.core.utils.StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX))
        {
            token = token.replaceFirst(TokenConstants.PREFIX, chat.aikf.common.core.utils.StringUtils.EMPTY);
        }
        return token;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        log.error("[会话认证失败] 路径: {}, 原因: {}", exchange.getRequest().getPath(), message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"error\":\"%s\"}", message);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        ).then(Mono.defer(response::setComplete));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}