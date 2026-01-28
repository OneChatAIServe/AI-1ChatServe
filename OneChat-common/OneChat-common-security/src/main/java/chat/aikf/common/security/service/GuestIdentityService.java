package chat.aikf.common.security.service;


import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.utils.uuid.IdUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 匿名认证凭证生成
 */
@Component
public class GuestIdentityService {


//    /**
//     * 访客token密钥
//     */
//    @Value("${one-chat.auth.visitor-token.secret}")
//    private String visitorTokenSecret;
//
//    /**
//     * 访客token有效期
//     */
//    @Value("${one-chat.auth.visitor-token.time:360}")
//    private int visitorTokenTime;

    /**
     * 访客短期token密钥
     */
    @Value("${one-chat.auth.visitor-session.secret}")
    private String visitorSessionSecret;


    /**
     * 访客短期token时效
     */
    @Value("${one-chat.auth.visitor-session.time:5}")
    private int visitorSessionTime;





//    /**
//     * 长期访客 Token，默认360天
//     * @param visitorId
//     * @return
//     */
//    public  String generateLongVisitorToken(String visitorId) {
//        Date expire = new Date(System.currentTimeMillis() + (long) visitorTokenTime * 24 * 60 * 60 * 1000);
//        return Jwts.builder()
//                .setSubject(visitorId)
//                .setExpiration(expire)
//                .signWith(SignatureAlgorithm.HS256,visitorTokenSecret)
//                .compact();
//    }


//    /**
//     * 解析长期访问token
//     * @param token
//     * @return
//     */
//    public Claims parseLongVisitorToken(String token) {
//
//        return Jwts.parser().setSigningKey(visitorTokenSecret).parseClaimsJws(token).getBody();
//    }


    /**
     * 短期会话 Token
     * @param visitorId
     * @return
     */
    public String generateSessionToken(String visitorId,String webStyleId) {
        Date expire = new Date(System.currentTimeMillis() + (long) visitorSessionTime * 60 * 1000);
        String sessionId = IdUtils.fastSimpleUUID();
        return Jwts.builder()
                .setId(sessionId)
                .setSubject(visitorId)
                .claim(SecurityConstants.KF_WEB_STYLE_ID, webStyleId)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, visitorSessionSecret)
                .compact();
    }


//    /**
//     * 解析短会话Token
//     * @param token
//     * @return
//     */
//    public Claims parseSessionToken(String token) {
//        return Jwts.parser().setSigningKey(visitorSessionSecret).parseClaimsJws(token).getBody();
//    }


}
