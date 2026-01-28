package chat.aikf.common.core.utils;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

public class SecureIdUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
//    private static final String SECRET_KEY = "aikf.chat"; // 请从配置读取！

    private static final int RANDOM_BYTES = 16;      // 128-bit random
    private static final int CHECKSUM_BYTES = 4;     // 32-bit checksum (足够防偶然碰撞)

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成一个带校验和的纯随机ID（40位十六进制字符串）
     * 格式: [32-char random][8-char checksum]
     */
    public static String generateSecureId(String secretKey) {
        // 1. 生成随机部分
        byte[] randomBytes = new byte[RANDOM_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        String randomHex = HexFormat.of().formatHex(randomBytes); // 32 chars

        // 2. 计算校验和（HMAC-SHA256 的前 4 字节）
        byte[] hmac = sign(randomHex,secretKey);
        String checksumHex = HexFormat.of().formatHex(hmac, 0, CHECKSUM_BYTES); // 8 chars

        return randomHex + checksumHex; // 40 chars total
    }

    /**
     * 验证 ID 是否合法
     * @return true 如果合法
     */
    public static boolean verifySecureId(String id,String secretKey) {
        if (id == null || id.length() != 40) {
            return false;
        }

        String randomPart = id.substring(0, 32);
        String providedChecksum = id.substring(32);

        // 重新计算校验和
        byte[] hmac = sign(randomPart,secretKey);
        String expectedChecksum = HexFormat.of().formatHex(hmac, 0, CHECKSUM_BYTES);

        // 恒定时间比较
        return MessageDigest.isEqual(
                providedChecksum.getBytes(StandardCharsets.UTF_8),
                expectedChecksum.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 获取原始 randomId（用于业务逻辑，如查数据库）
     */
    public static String extractRandomId(String secureId) {
        if (secureId == null || secureId.length() != 40) {
            return null;
        }
        return secureId.substring(0, 32);
    }

    // ====== 内部工具 ======

    private static byte[] sign(String data,String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec spec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(spec);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC failed", e);
        }
    }

}