package chat.aikf.im.allocation.service;

import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.exception.ServiceException;
import chat.aikf.im.allocation.enums.AllocateType;
import chat.aikf.im.allocation.enums.AssignStrategyType;
import chat.aikf.im.allocation.model.AllocateDto;
import chat.aikf.im.allocation.strategy.AssignStrategy;
import chat.aikf.im.allocation.strategy.LeastBusyAssignStrategy;
import chat.aikf.im.allocation.strategy.RandomAssignStrategy;
import chat.aikf.im.allocation.strategy.RoundRobinAssignStrategy;
import chat.aikf.im.tio.model.IdentityMsgDto;
import chat.aikf.ops.api.RemoteKfRuleService;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.EnumMap;
import java.util.Map;


@Service
@Slf4j
public class CustomerAssignService {


    @Autowired
    private RemoteKfRuleService remoteKfRuleService;








    private final Map<AssignStrategyType, AssignStrategy> strategyMap;

    public CustomerAssignService(
            RandomAssignStrategy random,
            RoundRobinAssignStrategy roundRobin,
            LeastBusyAssignStrategy leastBusy) {
        this.strategyMap = new EnumMap<>(AssignStrategyType.class);
        strategyMap.put(AssignStrategyType.RANDOM, random);
        strategyMap.put(AssignStrategyType.ROUND_ROBIN, roundRobin);
        strategyMap.put(AssignStrategyType.LEAST_BUSY, leastBusy);
    }


    /**
     * 根据分配策略获取分配规则
     * @param kfRuleId
     * @return
     */
    public AssignStrategy getStrategy(Long kfRuleId) {
        AssignStrategy strategy =null;

        R<OneChatKfRule> dbResult = remoteKfRuleService.findOneChatKfRule(kfRuleId, SecurityConstants.INNER);

        if (R.FAIL == dbResult.getCode()) {
            log.error("获取客服组规则异常:"+dbResult.getMsg());
            throw new ServiceException(dbResult.getMsg());
        }


        OneChatKfRule dbRule = dbResult.getData();
        if (dbRule != null) {
            //判断当前客服是否在工作时间
            if(OneChatKfRule.isRuleInWorkingHours(dbRule)){
                strategy = strategyMap.get(dbRule.getAllocateRule());
            }
        }

        return strategy;
    }


    /**
     * 获取接待访客的员工
     * @param webStyleId
     * @param visitorId
     * @return
     */
    public IdentityMsgDto getOnlineUserId(String webStyleId, String visitorId){


        R<OneChatKfRule> r = remoteKfRuleService.findOneChatKfRuleByWebStyleId(Long.valueOf(webStyleId), SecurityConstants.INNER);

        if (R.FAIL == r.getCode()) {
            log.error("获取网页客服样式对应的规则失败:webStyleId=" + webStyleId);
        }
        OneChatKfRule kfRule = r.getData();
        if(null == kfRule){
            log.error("网页客服对应客服规则组不存在:webStyleId=" + webStyleId);
        }else {

            IdentityMsgDto identityMsgDto = new IdentityMsgDto();
            identityMsgDto.setFromObj(visitorId);
            identityMsgDto.setKfRuleId(kfRule.getId().toString());
            identityMsgDto.setOneChatKfRule(kfRule);
            identityMsgDto.setWebStyleId(webStyleId);
            //判断当前客服是否在工作时间
            if (OneChatKfRule.isRuleInWorkingHours(kfRule)) {

                AllocateDto allocateDto = strategyMap.get(AssignStrategyType.fromCode(kfRule.getAllocateRule())).getOnlineAgentId(kfRule, visitorId,webStyleId);


                if (null != allocateDto.getAllocateState() && null != allocateDto.getRuleScope()) {

                    OneChatKfRuleScope ruleScope = allocateDto.getRuleScope();

                    if (allocateDto.getAllocateState() == AllocateType.ALLOCATE_TYPE_PD.getCode()) { //排队中
                        identityMsgDto.setReceptionState(OneChatVisitorSate.IDLE_STATE);
                        identityMsgDto.setToObj(ruleScope.getUserAccount());
                        identityMsgDto.setToObjName(ruleScope.getNickName());
                        identityMsgDto.setToObjavatar(ruleScope.getAvatar());
                        identityMsgDto.setMsgTip("当前客服正在服务其他客户，请耐心稍等");
                    } else if (allocateDto.getAllocateState() == AllocateType.ALLOCATE_TYPE_DH.getCode()) { //接待中）
                        identityMsgDto.setReceptionState(OneChatVisitorSate.RECEIVE_STATE);
                        identityMsgDto.setToObj(ruleScope.getUserAccount());
                        identityMsgDto.setToObjName(ruleScope.getNickName());
                        identityMsgDto.setToObjavatar(ruleScope.getAvatar());
                        identityMsgDto.setMsgTip(kfRule.getReceiveMsg());
                    }

                }

                return identityMsgDto;

            }else {//客服不在线
                identityMsgDto.setReceptionState(OneChatVisitorSate.OFFLINE_STATE);
                identityMsgDto.setToObjName("系统消息");
                identityMsgDto.setMsgTip(kfRule.getOfflineTipMsg());
                return identityMsgDto;
            }
        }

        return null;
    }


}
