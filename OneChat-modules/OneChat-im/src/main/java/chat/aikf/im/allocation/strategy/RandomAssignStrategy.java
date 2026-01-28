package chat.aikf.im.allocation.strategy;

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
import java.util.Random;
import java.util.stream.Collectors;


/**
 * 随机分配
 */
@Component
public class RandomAssignStrategy extends AssignStrategy {

    @Autowired
    protected RandomAssignStrategy(RemoteKfVisitorService remoteKfVisitorService, KfCacheRelUtils kfCacheRelUtils) {
        super(remoteKfVisitorService,kfCacheRelUtils);
    }

    @Override
    public AllocateDto getOnlineAgentId(OneChatKfRule oneChatKfRule, String visitorId,String webStyleId) {

             AllocateDto allocateDto=new AllocateDto();


            //设置每个客服的当前接待数
            List<OneChatKfRuleScope> ruleScopes = handleScopeUpperLimit( oneChatKfRule.getRuleScopeList(),webStyleId);
            oneChatKfRule.setRuleScopeList(ruleScopes);


            if(CollectionUtil.isEmpty(ruleScopes)){
                return allocateDto;
            }

            AllocateDto lastAllocateDto = lastReceptKf(oneChatKfRule, visitorId);

            if(null != lastAllocateDto){
                return lastAllocateDto;
            }

             OneChatKfRuleScope oneChatKfRuleScope = getRandomAvailableKf(ruleScopes);


            if(null != oneChatKfRuleScope){
                if("1".equals(oneChatKfRuleScope.getStatus())){
                    allocateDto.setAllocateState(AllocateType.ALLOCATE_TYPE_PD.getCode());
                }else{
                    allocateDto.setAllocateState(oneChatKfRuleScope.getUpperLimit()>oneChatKfRuleScope.getCurrentReceptionNumer()?AllocateType.ALLOCATE_TYPE_DH.getCode():AllocateType.ALLOCATE_TYPE_PD.getCode());
                }
                allocateDto.setRuleScope(oneChatKfRuleScope);
            }




         return allocateDto;
    }

    public static OneChatKfRuleScope getRandomAvailableKf(List<OneChatKfRuleScope> ruleScopes) {
        if (ruleScopes == null || ruleScopes.isEmpty()) {
            return null;
        }

        List<OneChatKfRuleScope> availableKfs = ruleScopes.stream()
//                .filter(kf -> !"1".equals(kf.getStatus()))
                .collect(Collectors.toList());

        if (!availableKfs.isEmpty()) {
            return availableKfs.get(new Random().nextInt(availableKfs.size()));
        }

        return ruleScopes.get(new Random().nextInt(ruleScopes.size()));
    }



}
