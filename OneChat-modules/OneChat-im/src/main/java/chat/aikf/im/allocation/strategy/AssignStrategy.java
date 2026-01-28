package chat.aikf.im.allocation.strategy;

import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.im.allocation.enums.AllocateType;
import chat.aikf.im.allocation.model.AllocateDto;
import chat.aikf.im.tio.starter.OneChatImStarter;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.utils.RuleFfServingUtils;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class AssignStrategy {


    protected RemoteKfVisitorService remoteKfVisitorService;


    protected KfCacheRelUtils kfCacheRelUtils;


   protected AssignStrategy(RemoteKfVisitorService remoteKfVisitorService, KfCacheRelUtils kfCacheRelUtils) {
        this.remoteKfVisitorService = remoteKfVisitorService;
        this.kfCacheRelUtils=kfCacheRelUtils;
    }


    /**
     * 获取上次接待人
     * @param oneChatKfRule
     * @param visitorId
     * @return
     */
    public   AllocateDto lastReceptKf(OneChatKfRule oneChatKfRule, String visitorId){

        AllocateDto allocateDto=new AllocateDto();

        if(StringUtils.isNotEmpty(oneChatKfRule.getHigherRule()) && oneChatKfRule.getHigherRule().equals("1")){ //优先匹配上次接待成员
            R<List<OneChatkfVisitor>> appointVisitorList = SpringUtils.getBean(RemoteKfVisitorService.class).findAppointVisitorList(OneChatkfVisitor.builder()
                    .visitorId(visitorId)
                    .kfRuleId(oneChatKfRule.getId())
                    .userAccount(oneChatKfRule.getRuleScopeList().stream()
                            .map(OneChatKfRuleScope::getUserAccount)
                            .filter(Objects::nonNull)
                            .filter(acc -> !acc.trim().isEmpty())
                            .map(String::trim)
                            .collect(Collectors.joining(",")))
                    .build(), SecurityConstants.INNER);
            if (R.FAIL == appointVisitorList.getCode()) {
                log.error("获取该访客接待客服失败:visitorId=" + visitorId+";kfRuleId="+oneChatKfRule.getId());
            }
            List<OneChatkfVisitor> visitors = appointVisitorList.getData();

            if(CollectionUtil.isNotEmpty(visitors)){

                //获取任意一个已接待但是未休息状态的客服
                OneChatKfRuleScope ruleScope = oneChatKfRule.getRuleScopeList().stream()
                        .findAny()
                        .orElse(null);
                if(null != ruleScope){
                    if("1".equals(ruleScope.getStatus())){
                        allocateDto.setAllocateState(AllocateType.ALLOCATE_TYPE_PD.getCode());
                    }else{
                        allocateDto.setAllocateState(ruleScope.getUpperLimit()>ruleScope.getCurrentReceptionNumer()?AllocateType.ALLOCATE_TYPE_DH.getCode():AllocateType.ALLOCATE_TYPE_PD.getCode());
                    }
                    allocateDto.setRuleScope(ruleScope);
                    return allocateDto;
                }
            }
        }


      return null;
    }
    /**
     * 分配客服给访客
     * @param oneChatKfRule 客服规则
     * @param visitorId 访客id
     * @param webStyleId 样式id
     * @return
     */
   public abstract AllocateDto getOnlineAgentId(OneChatKfRule oneChatKfRule, String visitorId,String webStyleId);


    /**
     * 处理员工接待上线（设置员工接待了多少人）
     * @param ruleScopeList
     * @param webStyleId
     * @return
     */
   public List<OneChatKfRuleScope> handleScopeUpperLimit(List<OneChatKfRuleScope> ruleScopeList,String webStyleId){
       List<OneChatKfRuleScope> newRuleScope=new ArrayList<>();

       if(CollectionUtil.isNotEmpty(ruleScopeList)){
           ruleScopeList.stream().forEach(item->{
                       item.setCurrentReceptionNumer(kfCacheRelUtils.getServingCountByKf(webStyleId,item.getKfRuleId().toString(),item.getUserAccount()));
                       newRuleScope.add(item);

           });

       }

       return newRuleScope;
   }




}
