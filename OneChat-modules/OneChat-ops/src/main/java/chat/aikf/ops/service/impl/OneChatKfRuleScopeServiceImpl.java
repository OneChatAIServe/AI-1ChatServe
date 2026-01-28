package chat.aikf.ops.service.impl;

import chat.aikf.common.core.constant.OneChatCacheKeyConstants;
import chat.aikf.common.redis.service.RedisService;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.ops.api.constant.OneChatKfState;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatKfRuleScopeService;
import chat.aikf.ops.mapper.OneChatKfRuleScopeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_rule_scope(客服规则组接待范围)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatKfRuleScopeServiceImpl extends ServiceImpl<OneChatKfRuleScopeMapper, OneChatKfRuleScope>
    implements IOneChatKfRuleScopeService {


    @Autowired
    private RedisService redisService;

    @Override
    public List<OneChatKfRuleScope> findOneChatKfRuleScopeByRuleId(Long ruleId) {
        return this.baseMapper.findOneChatKfRuleScopeByRuleId(ruleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKfSate(OneChatKfRuleScope oneChatKfRuleScope) {
        OneChatKfRuleScope ruleScope = this.getOne(new LambdaQueryWrapper<OneChatKfRuleScope>()
                .eq(OneChatKfRuleScope::getKfRuleId,oneChatKfRuleScope.getKfRuleId())
                .eq(OneChatKfRuleScope::getUserAccount, SecurityUtils.getUsername())
                .last("limit 1"));
        if(null != ruleScope){


            if (OneChatKfState.KF_STATUS_ONLINE==ruleScope.getKfStatus()) {
                ruleScope.setKfStatus(OneChatKfState.KF_STATUS_LEAVE);
            } else if (OneChatKfState.KF_STATUS_LEAVE==ruleScope.getKfStatus()) {
                ruleScope.setKfStatus(OneChatKfState.KF_STATUS_ONLINE);
            }
            if(this.updateById(ruleScope)){//清空规则缓存
                redisService.deleteObject(OneChatCacheKeyConstants.ImKeyGenerator.getKfRuleKey(oneChatKfRuleScope.getKfRuleId()));
            }
        }
    }
}




