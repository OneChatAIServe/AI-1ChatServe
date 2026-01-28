package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName one_chat_web_style
 */
@TableName(value ="one_chat_web_style")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatWebStyle extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 班组id
     */
    private Long kfRuleId;

    /**
     * 网页名称
     */
    private String name;

    /**
     * 样式内容客服代码
     */
    private String content;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}