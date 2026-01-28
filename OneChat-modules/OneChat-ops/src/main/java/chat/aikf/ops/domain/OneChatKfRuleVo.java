package chat.aikf.ops.domain;

import chat.aikf.ops.api.domain.OneChatKfRule;
import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfRuleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客服组id
     */
    private Long id;


    /**
     * 客服组名称
     */
    private String ruleName;


    /**
     * 客服组数据转化为vo
     * @param rules
     * @return
     */
    public static List<OneChatKfRuleVo> oneChatKfRuletoVO(List<OneChatKfRule> rules){
        List<OneChatKfRuleVo> ruleVos=new ArrayList<>();
        if(CollectionUtil.isNotEmpty(rules)){
            rules.stream().forEach(item->{
                ruleVos.add(OneChatKfRuleVo.builder()
                                .id(item.getId())
                                .ruleName(item.getRuleName())
                        .build());

            });
        }
        return ruleVos;

    }

}
