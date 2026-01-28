package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName one_chat_kf_visitor_tag_rel
 */
@TableName(value ="one_chat_kf_visitor_tag_rel")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfVisitorTagRel extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 访客id
     */
    private Long kfVisitorId;

    /**
     * 标签id
     */
    private Long tagId;


    //标签名
    @TableField(exist = false)
    private String tagName;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}