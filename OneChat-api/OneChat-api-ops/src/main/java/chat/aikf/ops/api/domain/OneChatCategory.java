package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName one_chat_category
 */
@TableName(value ="one_chat_category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatCategory extends BaseEntity {


    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 类型（1:客服分组；2:网页接入样式）
     */
    private Integer type;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}