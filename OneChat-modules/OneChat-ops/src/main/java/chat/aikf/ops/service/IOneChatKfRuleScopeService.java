package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_rule_scope(客服规则组接待范围)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatKfRuleScopeService extends IService<OneChatKfRuleScope> {



    /**
     * 根据规则id查询规则范围
     * @param ruleId
     * @return
     */
    List<OneChatKfRuleScope> findOneChatKfRuleScopeByRuleId(Long ruleId);


    /**
     * 修改客服状态
     * @param oneChatKfRuleScope
     */
    void updateKfSate(OneChatKfRuleScope oneChatKfRuleScope);

}
