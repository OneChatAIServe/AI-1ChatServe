package chat.aikf.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import chat.aikf.common.core.constant.CacheConstants;
import chat.aikf.common.core.constant.HttpStatus;
import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.constant.TokenConstants;
import chat.aikf.common.core.utils.JwtUtils;
import chat.aikf.common.core.utils.ServletUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.gateway.config.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关鉴权（增强版：支持 WebSocket 用户参数透传）
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();

        // 👇【新增】专门处理 /ws/user 开头的 WebSocket 请求
        if (url.startsWith("/ws/user")) {
            return handleWebSocketUserRequest(exchange, chain);
        }

//        if(url.startsWith("/file/chatMsgFile")){
//            List<String> clientType = request.getHeaders().get("client_type");
//            if(CollectionUtil.isNotEmpty(clientType)){
//                return chain.filter(exchange);
//            }
//        }

        // ========== 以下是你原有的全部逻辑，完全保留 ==========
        ServerHttpRequest.Builder mutate = request.mutate();

        // 跳过不需要验证的路径
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }

        String token = getToken(request, url);
        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        String userkey = JwtUtils.getUserKey(claims);
        boolean islogin = redisService.hasKey(getTokenKey(userkey));
        if (!islogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }
        String userid = JwtUtils.getUserId(claims);
        String username = JwtUtils.getUserName(claims);
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 设置用户信息到请求
        addHeader(mutate, SecurityConstants.USER_KEY, userkey);
        addHeader(mutate, SecurityConstants.DETAILS_USER_ID, userid);
        addHeader(mutate, SecurityConstants.DETAILS_USERNAME, username);
        // 内部请求来源参数清除
        removeHeader(mutate, SecurityConstants.FROM_SOURCE);
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    /**
     * 【新增】专门处理 /ws/user 的 WebSocket 请求：重写 URL 参数
     */
    private Mono<Void> handleWebSocketUserRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI originalUri = request.getURI();
        String query = originalUri.getQuery();

        // 1. 从 query 中获取 token（兼容你原有的 getToken 逻辑）
        String token = null;
        if (query != null) {
            Map<String, String> params = parseQueryString(query);
            token = params.get(SecurityConstants.SESSION_TOKEN_PARAM); // 默认是 "token"
        }

        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(exchange, "WebSocket 连接缺少 token");
        }

        // 2. 复用你原有的 JWT + Redis 校验逻辑
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        String userkey = JwtUtils.getUserKey(claims);
        if (!redisService.hasKey(getTokenKey(userkey))) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }
        String userid = JwtUtils.getUserId(claims);
        String username = JwtUtils.getUserName(claims);
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 3. 构造新的 query string（替换为明文用户参数）
        String newQuery = "userAccount=" + urlEncode(username)
                + "&userId=" + urlEncode(userid);

        // 4. 构建新 URI（路径不变，只换 query）
        URI newUri;
        try {
            newUri = new URI(
                    originalUri.getScheme(),
                    originalUri.getUserInfo(),
                    originalUri.getHost(),
                    originalUri.getPort(),
                    originalUri.getPath(),
                    newQuery,
                    originalUri.getFragment()
            );
        } catch (URISyntaxException e) {
            log.error("构造 WebSocket 新 URI 失败", e);
            return unauthorizedResponse(exchange, "请求格式错误");
        }

        // 5. 替换请求并继续过滤链
        ServerHttpRequest newRequest = request.mutate().uri(newUri).build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 解析 query string 为 Map
     */
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (StringUtils.isEmpty(query)) return params;
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            String key = pair[0];
            String value = pair.length == 2 ? urlDecode(pair[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    /**
     * URL 编码（使用你项目已有的工具）
     */
    private String urlEncode(String value) {
        return ServletUtils.urlEncode(value);
    }

    /**
     * URL 解码
     */
    private String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return value;
        }
    }

    // ========== 以下是你原有的方法，完全保留 ==========

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtils.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{},错误信息:{}", exchange.getRequest().getPath(), msg);
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }

    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    private String getToken(ServerHttpRequest request, String url) {
        String accessToken = null;
        if (url.startsWith("/ws/user")) {
            accessToken = request.getQueryParams().getFirst(SecurityConstants.SESSION_TOKEN_PARAM);
        }else if(url.startsWith("/file/avatars/") || url.startsWith("/file/chatMsgFile")){

            // 1. 获取请求中的所有Cookie
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            if (cookies != null && cookies.containsKey(SecurityConstants.AUTHORIZATION_HEADER)) {
                HttpCookie adminTokenCookie = cookies.getFirst(SecurityConstants.AUTHORIZATION_HEADER);
                if (adminTokenCookie != null) {
                    // 3. 成功获取到Cookie的值
                    accessToken = adminTokenCookie.getValue();
                }
            }

        }

        if(StringUtils.isEmpty(accessToken)){
            String token = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
                token = token.replaceFirst(TokenConstants.PREFIX, StringUtils.EMPTY);
            }
            return token;
        }
        return accessToken;
    }

    @Override
    public int getOrder() {
        return -200;
    }
}