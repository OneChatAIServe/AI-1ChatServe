package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.domain.OneChatKfRuleInitVo;
import chat.aikf.ops.domain.OneChatKfRuleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_rule(客服规则组)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatKfRuleService extends IService<OneChatKfRule> {


    /**
     * 获取客服组列表
     * @return
     */
    List<OneChatKfRuleVo> findList(OneChatKfRule oneChatKfRule);


    /**
     * 编辑客服组
     * @param oneChatKfRule
     * @return
     */
    void updateOneChatKfRule(OneChatKfRule oneChatKfRule);


    /**
     * 查询客服组详情(增加缓存机制)
     * @param id
     * @return
     */
    OneChatKfRule findOneChatKfRule(Long id);


    /**
     * 通过网页客服样式id获取对应的规则
     * @param id
     * @return
     */
    OneChatKfRule findOneChatKfRuleByWebStyleId(Long id);


    /**
     * 删除规则组
     * @param ids
     */
    void removeOneChatKfRule(String[] ids);


    /**
     * 获取当前客服信息
     * @return
     */
    OneChatKfRuleInitVo findCurrentKfInfo();

}
