package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @TableName one_chat_kf_visitor_msg
 */
@TableName(value ="one_chat_kf_visitor_msg")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfVisitorMsg extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 访客id
     */
    private String kfVisitorId;

    /**
     * 消息热送人id
     */
    private String fromObj;

    /**
     * 消息发送人名称
     */
//    @TableField(exist = false)
//    private String fromObjName;
//
//
//
//    /**
//     * 消息发送人名称
//     */
//    @TableField(exist = false)
//    private String fromObjavatar;

    /**
     * 消息接受人id
     */

    private String toObj;

//    /**
//     * 消息接受人名称
//     */
//    @TableField(exist = false)
//    private String toObjName;
//
//
//    /**
//     * 消息接受人头像
//     */
//    @TableField(exist = false)
//    private String toObjavatar;


    /**
     * 展示名称
     */
    @TableField(exist = false)
    private String showName;


    /**
     * 展示头像
     */
    @TableField(exist = false)
    private String showAvatar;

    /**
     * 客服规则组id
     */
    private Long kfRuleId;

    /**
     * OneChatMsgTypes 消息类型(text文字  emotion表情  image图片)
     */
    private String msgType;


    /**
     * 消息来源 0:访客 1:员工客服
     */
    private Integer msgSource;

    /**
     * 接受内容
     */
    private String content;


    /**
     * 是否已读:0:未读;1:已读
     */
    private Integer readReceipt;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}