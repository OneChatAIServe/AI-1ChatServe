package chat.aikf.auth.controller;

import chat.aikf.auth.form.GuestLoginBody;
import chat.aikf.common.core.config.OneChatAuthConfig;
import chat.aikf.common.core.config.OneChatConfig;
import chat.aikf.common.core.constant.Constants;
import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.utils.NonceUtils;
import chat.aikf.common.core.utils.SecureIdUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.security.service.GuestIdentityService;
import chat.aikf.common.security.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
public class GuestTokenController {





    /**
     * 访客唯一标识的密钥
     */
    @Value("${one-chat.auth.visitor-id.secret:aikf.chat}")
    private String visitorIdSecret;


    @Autowired
    private OneChatConfig chatConfig;


    @Autowired
    private OneChatAuthConfig oneChatAuthConfig;

    @Autowired
    private GuestIdentityService guestIdentityService;




    /**
     * 申请唯一凭证，识别访客
     * @param request
     * @return
     */
    @GetMapping("/identify")
    public R<Map<String, Object>> identifyGuest(HttpServletRequest request) {

        if (!isValidReferer(request,oneChatAuthConfig.getAllowedDomains())) {
            return R.fail(403,"域名未授权");
        }

        // 构造你的统一返回对象
        return R.ok(Map.of(
                "visitor_id",SecureIdUtils.generateSecureId(visitorIdSecret),
                "message", "访客身份已创建"
        ));
    }


    /**
     *  获取短期会话 Token（聊天会话）
     * @param guestLoginBody
     * @return
     */
    @PostMapping("/session")
    public R<Map<String, Object>> createSession(@Valid @RequestBody GuestLoginBody guestLoginBody, HttpServletRequest request) {


        if (!isValidReferer(request,oneChatAuthConfig.getAllowedDomains())) {
            return R.fail(403,"域名未授权");
        }

        if(!SecureIdUtils.verifySecureId(guestLoginBody.getVisitorId(),visitorIdSecret)){
            return R.fail(401,"无效访客身份");
        }
        try {

            String sessionToken = guestIdentityService.generateSessionToken(guestLoginBody.getVisitorId(),guestLoginBody.getWebStyleId());

            return R.ok(Map.of(
                    "session_token", sessionToken,
                    "expires_in", 5 * 60,
                    "chat_session_time",chatConfig.getSessionTime()
            ));
        } catch (Exception e) {
            return R.fail(401,"无效访客身份");
        }
    }


    public boolean isValidReferer(HttpServletRequest request, List<String> allowedDomains) {
        String referer = request.getHeader(Constants.REFERER);

        if (referer == null || allowedDomains == null || allowedDomains.isEmpty()) {
            return false;
        }

        String refererHost;
        try {
            URI uri = new URI(referer);
            refererHost = uri.getHost();
            if (refererHost == null) {
                return false;
            }
        } catch (URISyntaxException e) {
            return false; // Invalid URL
        }

        // 精确匹配（推荐）
        return allowedDomains.contains(refererHost);

        // 或者使用子域名匹配（按需选择）
    /*
    return allowedDomains.stream().anyMatch(domain -> {
        if (domain.equals(refererHost)) return true;
        return domain.startsWith(".") && refererHost.endsWith(domain);
    });
    */
    }








}