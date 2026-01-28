package chat.aikf.im.allocation.strategy;

import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.im.allocation.enums.AllocateType;
import chat.aikf.im.allocation.model.AllocateDto;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;


/**
 * 轮流分配
 */
@Component
public class RoundRobinAssignStrategy extends AssignStrategy {

    @Autowired
    private KfCacheRelUtils kfCacheRelUtils;


    @Autowired
    protected RoundRobinAssignStrategy(RemoteKfVisitorService remoteKfVisitorService,KfCacheRelUtils kfCacheRelUtils) {
        super(remoteKfVisitorService,kfCacheRelUtils);
    }

    @Override
    public AllocateDto getOnlineAgentId(OneChatKfRule oneChatKfRule, String visitorId,String webStyleId) {
        AllocateDto allocateDto=new AllocateDto();

        List<OneChatKfRuleScope> ruleScopes = handleScopeUpperLimit(oneChatKfRule.getRuleScopeList(),webStyleId);
        oneChatKfRule.setRuleScopeList(ruleScopes);


            if(CollectionUtil.isEmpty(ruleScopes)){
                 return allocateDto;
            }

            AllocateDto lastAllocateDto = lastReceptKf(oneChatKfRule, visitorId);

            if(null != lastAllocateDto){
                return lastAllocateDto;
            }


            //获取上一个分配的客服
            String kfUserAccount = kfCacheRelUtils.getCurrentRuleAllocateKf(webStyleId,oneChatKfRule.getId().toString());

            //如果达到接待上线获取客服离开则继续下一个
            OneChatKfRuleScope ruleScope = findNextAvailableAgentAfter(ruleScopes, kfUserAccount);
                if(null != ruleScope){
                    if("1".equals(ruleScope.getStatus())){
                        allocateDto.setAllocateState(AllocateType.ALLOCATE_TYPE_PD.getCode());
                    }else{
                        allocateDto.setAllocateState(ruleScope.getUpperLimit()>ruleScope.getCurrentReceptionNumer()?AllocateType.ALLOCATE_TYPE_DH.getCode():AllocateType.ALLOCATE_TYPE_PD.getCode());
                    }
                    allocateDto.setRuleScope(ruleScope);
                }



            return allocateDto;
    }


    //根据指定账号轮流分配
    public static OneChatKfRuleScope  findNextAvailableAgentAfter(
            List<OneChatKfRuleScope> list,
            String targetUserAccount) {

        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        // 情况1: lastUserAccount 为空 → 返回第一个
        if (StringUtils.isEmpty(targetUserAccount)) {
            return list.get(0);
        }

        // 情况2: 查找 lastUserAccount 在集合中的位置
        int lastIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (targetUserAccount.equals(list.get(i).getUserAccount())) {
                lastIndex = i;
                break;
            }
        }

        // 情况3: 没找到（账号不在集合中）→ 返回第一个
        if (lastIndex == -1) {
            return list.get(0);
        }

        // 情况4: 找到 → 返回下一个（循环）
        int nextIndex = (lastIndex + 1) % list.size();
        return list.get(nextIndex);
    }

}