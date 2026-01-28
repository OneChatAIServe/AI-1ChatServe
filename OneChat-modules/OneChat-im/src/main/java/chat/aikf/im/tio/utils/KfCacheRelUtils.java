package chat.aikf.im.tio.utils;


import chat.aikf.common.core.config.OneChatConfig;
import chat.aikf.common.core.constant.OneChatCacheKeyConstants;
import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.im.tio.model.GuestIdentityMsgDto;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.utils.RuleFfServingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 客服业务缓存工具类
 */
@Component
public class KfCacheRelUtils {


    @Autowired
    private RedisService redisService;

    @Autowired
    private OneChatConfig oneChatConfig;


    @Autowired
    private RuleFfServingUtils ruleFfServingUtils;


    /**
     * 访客连接成功后设置的初始化缓存
     * @param msgDto
     * @param webStyleId
     */
    public void linkInitCache(GuestIdentityMsgDto msgDto,String webStyleId){

        //连接kfSession数据
        String initKey = OneChatCacheKeyConstants.ImKeyGenerator.getInitVisitorSessionKey(msgDto.getVisitorId(),webStyleId);
        redisService.setCacheObject(initKey,msgDto, oneChatConfig.sessionTime.longValue() , TimeUnit.MINUTES);


        //构建指定样式规则下,客服与访客的连接关系
        ruleFfServingUtils.bindVisitorToKf(webStyleId,msgDto.getKfRuleId(),msgDto.getReceptObjId(),msgDto.getVisitorId(),oneChatConfig.sessionTime.longValue());


        //设置指定样式下的访客规则已经分配到了哪个员工
        String allocateKfKey = OneChatCacheKeyConstants.ImKeyGenerator.getCurrentRuleAllocateKfKey(webStyleId,msgDto.getKfRuleId());
        redisService.setCacheObject(allocateKfKey,msgDto.getReceptObjId());

    }


    /**
     * 访客连接成功后设置的初始化缓存
     * @param visitorId
     * @param webStyleId
     */
    public void removeLinkInitCache(String kfRuleId,String userAccount,String visitorId,String webStyleId){

        //删除指定访客的kfsession
        String initKey = OneChatCacheKeyConstants.ImKeyGenerator.getInitVisitorSessionKey(visitorId,webStyleId);
        redisService.deleteObject(initKey);

        //接触指定样式-客服规则下访客与客服的连接关系
        ruleFfServingUtils.unbindVisitorFromKf(webStyleId,kfRuleId,userAccount,visitorId);
    }


    /**
     * 更新初始化连接数据
     * @param visitorId
     * @param webStyleId
     */
    public void updateLinkInitCache(String visitorId,String webStyleId){

        String initKey =OneChatCacheKeyConstants.ImKeyGenerator.getInitVisitorSessionKey(visitorId,webStyleId);

        GuestIdentityMsgDto msgDto = SpringUtils.getBean(RedisService.class).getCacheObject(initKey);
        if(null != msgDto){
            msgDto.setInitState(1);
            msgDto.setVisitorMsg(null); //设置为空避免接入语重复
            redisService.setCacheObject(initKey,msgDto, oneChatConfig.sessionTime.longValue() , TimeUnit.MINUTES);
        }

    }


    /**
     * 获取指定样式客服规则已经分配到了哪个员工账号
     * @param webStyleId
     * @param kfRuleId
     * @return
     */
    public String getCurrentRuleAllocateKf(String webStyleId,String kfRuleId){
        String allocateKfKey = OneChatCacheKeyConstants.ImKeyGenerator.getCurrentRuleAllocateKfKey(webStyleId,kfRuleId);

        return  (String)redisService.getCacheObject(allocateKfKey);
    }


    /**
     * 获取指定样式下的客服规则-员工接待了多少人
     * @param webStyleId
     * @param kfRuleId
     * @param userAccount
     * @return
     */
    public long getServingCountByKf(String webStyleId,String kfRuleId, String userAccount){

       return ruleFfServingUtils.getServingCountByKf(webStyleId,kfRuleId,userAccount);
    }


}