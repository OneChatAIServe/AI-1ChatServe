package chat.aikf.ops.service.impl;

import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.common.core.constant.OneChatCacheKeyConstants;
import chat.aikf.common.core.exception.ServiceException;
import chat.aikf.common.core.utils.NumberUtils;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import chat.aikf.ops.api.domain.OneChatWebStyle;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.domain.OneChatKfRuleInitVo;
import chat.aikf.ops.domain.OneChatKfRuleVo;
import chat.aikf.ops.service.IOneChatKfRuleScopeService;
import chat.aikf.ops.service.IOneChatWebStyleService;
import chat.aikf.ops.service.IOneChatkfVisitorService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatKfRuleService;
import chat.aikf.ops.mapper.OneChatKfRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author robin
* @description 针对表【one_chat_kf_rule(客服规则组)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
@Slf4j
public class OneChatKfRuleServiceImpl extends ServiceImpl<OneChatKfRuleMapper, OneChatKfRule>
    implements IOneChatKfRuleService {

    @Autowired
    private IOneChatKfRuleScopeService oneChatKfRuleScopeService;


    @Autowired
    private IOneChatkfVisitorService oneChatkfVisitorService;


    @Autowired
    private IOneChatWebStyleService oneChatWebStyleService;


    @Autowired
    private RedisService redisService;


    @Override
    @DataScope
    public List<OneChatKfRuleVo> findList(OneChatKfRule oneChatKfRule) {

        List<OneChatKfRule> ruleScopes = this.list(new LambdaQueryWrapper<OneChatKfRule>()
                .orderByDesc(OneChatKfRule::getCreateTime));

        return  OneChatKfRuleVo.oneChatKfRuletoVO(ruleScopes);
    }

    @Override
    @Transactional
    public void updateOneChatKfRule(OneChatKfRule oneChatKfRule) {

        if(this.saveOrUpdate(oneChatKfRule)){


            List<OneChatKfRuleScope> ruleScopeList =
                    oneChatKfRule.getRuleScopeList();
            if(CollectionUtil.isNotEmpty(ruleScopeList)){

                //移除不存在的
                List<Long> idList = ruleScopeList.stream()
                        .filter(Objects::nonNull)
                        .peek(item -> {
                            item.setKfRuleId(oneChatKfRule.getId());
                            item.setUserAccount(SecurityUtils.getUsername());
                        })
                        .filter(item -> item != null && item.getId() != null)
                        .map(OneChatKfRuleScope::getId)
                        .collect(Collectors.toList());
                oneChatKfRuleScopeService.remove(new LambdaQueryWrapper<OneChatKfRuleScope>()
                          .eq(OneChatKfRuleScope::getKfRuleId,oneChatKfRule.getId())
                        .notIn(CollectionUtil.isNotEmpty(idList),OneChatKfRuleScope::getId,idList));

                oneChatKfRuleScopeService.saveOrUpdateBatch(ruleScopeList);
            }
            this.clearRuleCacheBatch(new Long[]{oneChatKfRule.getId()});;
        }
    }

    @Override
    public OneChatKfRule findOneChatKfRule(Long id) {

        String cacheKey = OneChatCacheKeyConstants.ImKeyGenerator.getKfRuleKey(id);
        OneChatKfRule cachedRule = redisService.getCacheObject(cacheKey);

        if (cachedRule != null) {
            log.debug("从缓存获取客服规则: ruleId={}, ruleName={}", id, cachedRule.getRuleName());
            if(null == cachedRule.getId()){
                return null;
            }
            return cachedRule;
        }


        // 3. 缓存未命中，从数据库查询
        log.debug("缓存未命中，从数据库查询客服规则: ruleId={}", id);

        OneChatKfRule kfRule = this.getById(id);

        if(null != kfRule){
            kfRule.setRuleScopeList(
                    oneChatKfRuleScopeService
                            .findOneChatKfRuleScopeByRuleId(kfRule.getId())
            );

            //4. 将数据库查询结果存入缓存（设置过期时间） 15分钟
            redisService.setCacheObject(cacheKey, kfRule, OneChatCacheKeyConstants.CacheTTL.KF_RULE, TimeUnit.SECONDS);
            log.info("客服规则已缓存: ruleId={}, ruleName={}, expire={}s",
                    id, kfRule.getRuleName(), OneChatCacheKeyConstants.CacheTTL.KF_RULE);
        }else{
            // 数据库查询失败，缓存空值防止缓存穿透
            redisService.setCacheObject(cacheKey, new OneChatKfRule(),  OneChatCacheKeyConstants.CacheTTL.CACHE_PENETRATE_TTL, TimeUnit.SECONDS);
        }

        return kfRule;
    }

    @Override
    public OneChatKfRule findOneChatKfRuleByWebStyleId(Long id) {
        String kfStyleWebkey = OneChatCacheKeyConstants.ImKeyGenerator.getKfStyleWebkey(id);
        OneChatWebStyle oneChatWebStyle=redisService.getCacheObject(kfStyleWebkey);
        if(null == oneChatWebStyle){
            OneChatWebStyle newOneChatWebStyle = oneChatWebStyleService.getById(id);
            if(null != newOneChatWebStyle){
                redisService.setCacheObject(kfStyleWebkey, newOneChatWebStyle, OneChatCacheKeyConstants.CacheTTL.KF_STYLE_WEB_TTL, TimeUnit.SECONDS);
                oneChatWebStyle=newOneChatWebStyle;
            }else{
                // 数据不存在：缓存空对象（防止穿透），TTL 较短
                redisService.setCacheObject(
                        kfStyleWebkey,
                        new OneChatWebStyle(),
                        OneChatCacheKeyConstants.CacheTTL.CACHE_PENETRATE_TTL, // 空缓存 TTL：建议 30~300 秒，避免长期污染
                        TimeUnit.SECONDS
                );
            }
        }

        if(null == oneChatWebStyle.getKfRuleId()){
            return null;
        }


        return findOneChatKfRule(oneChatWebStyle.getKfRuleId());
    }

    @Override
    @Transactional
    public void removeOneChatKfRule(String[] ids) throws ServiceException{
        if(oneChatWebStyleService.count(new LambdaQueryWrapper<OneChatWebStyle>()
                .in(OneChatWebStyle::getKfRuleId,ids))>0){
            throw new ServiceException("当前客服组规则正在被使用不可删除");
        }
        this.removeByIds(Arrays.asList(ids));
        oneChatKfRuleScopeService.remove(new LambdaQueryWrapper<OneChatKfRuleScope>()
                .in(OneChatKfRuleScope::getKfRuleId,ids));
        this.clearRuleCacheBatch(Arrays.stream(ids)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toArray(Long[]::new));
    }

    @Override
    public OneChatKfRuleInitVo findCurrentKfInfo() {

        OneChatKfRuleScope ruleScope = oneChatKfRuleScopeService.getOne(new LambdaQueryWrapper<OneChatKfRuleScope>()
                .eq(OneChatKfRuleScope::getUserAccount, SecurityUtils.getUsername())
                .last("limit 1"));


        if(null != ruleScope){
            OneChatKfRule oneChatKfRule
                    = this.findOneChatKfRule(ruleScope.getKfRuleId());
            if(null != oneChatKfRule){

                OneChatWebStyle webStyle = oneChatWebStyleService.getOne(new LambdaQueryWrapper<OneChatWebStyle>()
                        .eq(OneChatWebStyle::getKfRuleId, oneChatKfRule.getId())
                        .last("limit 1"));

                if(null != webStyle){
                    return  OneChatKfRuleInitVo.builder()
                            .currentKfState(ruleScope.getKfStatus())
                            .ruleName(oneChatKfRule.getRuleName())
                            .nickName(ruleScope.getNickName())
                            .upperLimit(ruleScope.getUpperLimit())
                            .currentRecepNum(
                                    NumberUtils.toInteger(oneChatkfVisitorService.count(new LambdaQueryWrapper<OneChatkfVisitor>()
                                            .eq(OneChatkfVisitor::getKfRuleId,ruleScope.getKfRuleId())
                                            .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.RECEIVE_STATE)
                                            .eq(OneChatkfVisitor::getUserAccount,ruleScope.getUserAccount())))
                            )
                            .lineUpNum(
                                    NumberUtils.toInteger(oneChatkfVisitorService.count(new LambdaQueryWrapper<OneChatkfVisitor>()
                                            .eq(OneChatkfVisitor::getKfRuleId,ruleScope.getKfRuleId())
                                            .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.IDLE_STATE)
                                            .eq(OneChatkfVisitor::getUserAccount,ruleScope.getUserAccount())))
                            )
                            .kfRuleId(oneChatKfRule.getId())
                            .webStyleId(webStyle.getId().toString())
                            .avatar(ruleScope.getAvatar())
                            .userAccount(ruleScope.getUserAccount())
                            .receiveMsg(oneChatKfRule.getReceiveMsg())
                            .endMsg(oneChatKfRule.getEndMsg())
                            .build();
                }
            }

        }

        return null;
    }


    //批量清除客服规则缓存
    private void clearRuleCacheBatch(Long[] ruleIds) {
        if (ruleIds != null && ruleIds.length > 0) {
            for (Long ruleId : ruleIds) {

                String ruleCacheKey = OneChatCacheKeyConstants.ImKeyGenerator.getKfRuleKey(ruleId);
                log.info("ruleCacheKey="+ruleCacheKey);
                redisService.deleteObject(ruleCacheKey);
            }
            log.info("客服规则缓存批量清除: ruleIds={}", Arrays.toString(ruleIds));
        }
    }
}




