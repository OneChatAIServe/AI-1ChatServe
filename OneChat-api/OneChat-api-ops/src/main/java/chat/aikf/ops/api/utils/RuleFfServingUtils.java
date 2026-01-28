package chat.aikf.ops.api.utils;

import chat.aikf.common.core.constant.OneChatCacheKeyConstants;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 客服规则组-客服-访客关系维护
 */
@Component
public class RuleFfServingUtils {

    @Autowired
    private RedisService redisService;





    /**
     * 绑定：规则 + 客服 + 访客
     */
    public void bindVisitorToKf(String webStyleId,String kfRuleId, String userAccount, String visitorId,long expireTime) {
        if (StringUtils.isAnyEmpty(webStyleId,kfRuleId, userAccount, visitorId)) {
            return;
        }
        String key = OneChatCacheKeyConstants.ImKeyGenerator.getKfServingKey(webStyleId,kfRuleId,userAccount);
        redisService.setCacheMapValue(key, visitorId, "1");
        redisService.expire(key, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 解绑：移除指定三层关系
     */
    public boolean unbindVisitorFromKf(String webStyleId,String kfRuleId, String userAccount, String visitorId) {
        if (StringUtils.isAnyEmpty(webStyleId,kfRuleId, userAccount, visitorId)) {
            return false;
        }
        String key = OneChatCacheKeyConstants.ImKeyGenerator.getKfServingKey(webStyleId,kfRuleId,userAccount);
        return redisService.deleteCacheMapValue(key, visitorId);
    }

    /**
     * 获取某规则下某客服的接待人数
     */
    public long getServingCountByKf(String webStyleId,String kfRuleId, String userAccount) {
        if (StringUtils.isAnyEmpty(webStyleId,kfRuleId, userAccount)) {
            return 0L;
        }
        String key = OneChatCacheKeyConstants.ImKeyGenerator.getKfServingKey(webStyleId,kfRuleId,userAccount);
        return redisService.getHashSize(key); // 需要补充 getHashSize 方法（见下文）
    }


    /**
     * （可选）获取某规则下某客服的所有访客
     */
    public Set<String> getVisitorsByKf(String webStyleId,String kfRuleId, String userAccount) {
        if (StringUtils.isAnyEmpty(webStyleId,kfRuleId, userAccount)) {
            return Collections.emptySet();
        }
        String key = OneChatCacheKeyConstants.ImKeyGenerator.getKfServingKey(webStyleId,kfRuleId,userAccount);
        Map<String, Object> map = redisService.getCacheMap(key);
        return map != null ? map.keySet() : Collections.emptySet();
    }
}
