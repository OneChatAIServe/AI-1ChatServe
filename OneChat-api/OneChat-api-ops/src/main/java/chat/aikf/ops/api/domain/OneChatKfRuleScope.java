package chat.aikf.ops.api.domain;

import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.web.domain.BaseEntity;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @TableName one_chat_kf_rule_scope
 */
@TableName(value ="one_chat_kf_rule_scope")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfRuleScope extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 员工id
     */
    private Long userId;


    /**
     * 客服头像
     */
    private String avatar;

    /**
     * 对外昵称
     */
    private String nickName;


    /**
     * 员工账号
     */
    private String userAccount;


    /**
     * 员工名称
     */
    @TableField(exist = false)
    private String userName;


    /**
     * 账号状态（0正常 1离开）
     */
    @TableField(exist = false)
    private String status;


    /**
     * 客服状态(0:离开;1:在线;2:停用)
     */
    private Integer kfStatus;

    /**
     * 上限
     */
    private Integer upperLimit;

    /**
     * 客服组规则id
     */
    private Long kfRuleId;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


    /**
     * 当前接待数
     */
    @TableField(exist = false)
    private long currentReceptionNumer;






    /**
     * 获取未达到接待上线的客服
     * @param ruleScopes
     * @return
     */
    public static List<OneChatKfRuleScope> findNoUpLimitRuleScope(  List<OneChatKfRuleScope> ruleScopes){


       return ruleScopes.stream()
                .filter(kf -> kf.getUpperLimit() != null && kf.getCurrentReceptionNumer() < kf.getUpperLimit())
                .toList();
    }



}
