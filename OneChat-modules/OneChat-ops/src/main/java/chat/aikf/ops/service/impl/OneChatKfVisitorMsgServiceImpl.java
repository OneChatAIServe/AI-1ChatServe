package chat.aikf.ops.service.impl;

import chat.aikf.ops.api.constant.OneChatReadMsgState;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.service.IOneChatKfRuleScopeService;
import chat.aikf.ops.service.IOneChatkfVisitorService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatKfVisitorMsgService;
import chat.aikf.ops.mapper.OneChatKfVisitorMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author robin
* @description 针对表【one_chat_kf_visitor_msg(访客回话表)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatKfVisitorMsgServiceImpl extends ServiceImpl<OneChatKfVisitorMsgMapper, OneChatKfVisitorMsg>
    implements IOneChatKfVisitorMsgService {

    @Autowired
    private IOneChatkfVisitorService oneChatkfVisitorService;

    @Autowired
    private IOneChatKfRuleScopeService oneChatKfRuleScopeService;

    @Override
    public void addVisitorMsg(OneChatKfVisitorMsg oneChatKfVisitorMsg) {
        this.save(oneChatKfVisitorMsg);
    }

    @Override
    public List<OneChatKfVisitorMsg> getMsgList(Long kfVisitorId) {
        List<OneChatKfVisitorMsg> kfVisitorMsgs = list(new LambdaQueryWrapper<OneChatKfVisitorMsg>()
                .eq(OneChatKfVisitorMsg::getKfVisitorId, kfVisitorId));
        if(CollectionUtil.isNotEmpty(kfVisitorMsgs)){






            OneChatkfVisitor oneChatkfVisitor = oneChatkfVisitorService.getById(kfVisitorId);

            OneChatKfRuleScope ruleScope = oneChatKfRuleScopeService.getOne(new LambdaQueryWrapper<OneChatKfRuleScope>()
                    .eq(OneChatKfRuleScope::getKfRuleId, oneChatkfVisitor.getKfRuleId())
                    .eq(OneChatKfRuleScope::getUserAccount, oneChatkfVisitor.getUserAccount())
                    .last("limit 1"));


            kfVisitorMsgs.stream().forEach(item->{


                if(0==item.getMsgSource() && null != oneChatkfVisitor){ //访客
                    item.setShowName(oneChatkfVisitor.getName());
                    item.setShowAvatar(oneChatkfVisitor.getAvatar());
                }else if(1==item.getMsgSource() && null != ruleScope){  //客服
                    item.setShowName(ruleScope.getNickName());
                    item.setShowAvatar(ruleScope.getAvatar());
                }
            });


            //更新未读消息为已读状态
            List<Long> unreadMessageIds = kfVisitorMsgs.stream()
                    .filter(msg -> msg.getReadReceipt() != null && msg.getReadReceipt() == 0)
                    .map(OneChatKfVisitorMsg::getId)
                    .collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(unreadMessageIds)){
                this.update(OneChatKfVisitorMsg.builder()
                                .readReceipt(OneChatReadMsgState.readReceipt)
                        .build(), new LambdaQueryWrapper<OneChatKfVisitorMsg>()
                        .in(OneChatKfVisitorMsg::getId,unreadMessageIds));

            }




        }

        return kfVisitorMsgs;
    }
}




