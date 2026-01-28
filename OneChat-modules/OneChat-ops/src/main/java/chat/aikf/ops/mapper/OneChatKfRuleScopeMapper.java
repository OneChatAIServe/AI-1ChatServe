package chat.aikf.ops.mapper;

import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_rule_scope(客服规则组接待范围)】的数据库操作Mapper
* @createDate 2025-12-12 11:04:32
* @Entity chat.aikf.ops.domain.OneChatKfRuleScope
*/
public interface OneChatKfRuleScopeMapper extends BaseMapper<OneChatKfRuleScope> {


    /**
     * 根据规则id查询规则范围
     * @param ruleId
     * @return
     */
    List<OneChatKfRuleScope> findOneChatKfRuleScopeByRuleId(@Param("ruleId") Long ruleId);

}




