package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName one_chat_tag
 */
@TableName(value ="one_chat_tag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatTag extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签组id
     */
    private Long tagGroupId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}
