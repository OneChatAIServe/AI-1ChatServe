package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @TableName one_chat_tag_group
 */
@TableName(value ="one_chat_tag_group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatTagGroup extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 标签组名称
     */
    private String tagGroupName;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;


    /**
     * 标签
     */
    @TableField(exist = false)
    private List<OneChatTag> chatTagList;

}