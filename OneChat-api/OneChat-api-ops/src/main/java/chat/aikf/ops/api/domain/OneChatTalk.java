package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName one_chat_talk
 */
@TableName(value ="one_chat_talk")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatTalk extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 话术内容
     */
    private String content;

    /**
     * 消息类型(text文字)
     */
    private String type;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}
