package chat.aikf.im.allocation.model;

import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import lombok.Data;

@Data
public class AllocateDto {

    //状态 AllocateType 1:排队 2:对话
    private Integer allocateState;

    //具体分配的客服
    private  OneChatKfRuleScope ruleScope;
}
