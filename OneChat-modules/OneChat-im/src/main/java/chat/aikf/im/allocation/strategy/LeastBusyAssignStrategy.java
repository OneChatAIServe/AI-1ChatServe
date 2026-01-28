package chat.aikf.im.allocation.strategy;

import chat.aikf.im.allocation.enums.AllocateType;
import chat.aikf.im.allocation.model.AllocateDto;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import chat.aikf.ops.api.utils.RuleFfServingUtils;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * 空闲分配
 */
@Component
public class LeastBusyAssignStrategy extends AssignStrategy {

    @Autowired
    protected LeastBusyAssignStrategy(RemoteKfVisitorService remoteKfVisitorService,KfCacheRelUtils kfCacheRelUtils) {
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

            OneChatKfRuleScope ruleScope = findLeastLoadedRule(ruleScopes).get();

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


    /**
     * 空闲分配规则
     */
    public static Optional<OneChatKfRuleScope> findLeastLoadedRule(List<OneChatKfRuleScope> ruleScopes) {


        // 收集所有具有最小值的对象，然后随机选择一个[1,3](@ref)
        List<OneChatKfRuleScope> minObjects = ruleScopes.stream()
                .filter(obj -> obj != null)
                .collect(Collectors.groupingBy(
                        obj -> obj.getCurrentReceptionNumer(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .min(Comparator.comparingInt(entry -> Math.toIntExact(entry.getKey())))
                .map(entry -> entry.getValue())
                .orElse(List.of());

        if (minObjects.isEmpty()) {
            return Optional.empty();
        }

        // 随机选择一个[11](@ref)
        Random random = new Random();
        return Optional.of(minObjects.get(random.nextInt(minObjects.size())));
    }

}
