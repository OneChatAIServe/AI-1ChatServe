package chat.aikf.ops.api.domain;

import chat.aikf.common.core.utils.IpLocationUtils;
import chat.aikf.common.core.web.domain.BaseEntity;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @TableName one_chat_ kf_visitor
 */
@TableName(value ="one_chat_kf_visitor")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatkfVisitor extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;


    /**
     * 访客名称
     */
    private String name;


    /**
     * 对应前端访客的唯一标识
     */
    private String visitorId;


    /**
     * 客服规则id
     */
    private Long kfRuleId;


    /**
     * 接待人账号
     */
    private String userAccount;


    /**
     * 访客ip
     */
    private String ipaddr;


    /**
     * 访客真实ip
     */
    private String ipRealAddr;

    /**
     * 当前访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date currentViewTime;

    /**
     * 首次访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstViewTime;

    /**
     * 访问次数
     */
    private Integer viewNumber;

    /**
     * OneChatChannelTypes 渠道类型（1:网页）
     */
    private Integer channelType;

    /**
     * 访问设备
     */
    private String viewDevice;


    /**
     * 访客操作系统
     */
    private String viewOs;

    /**
     * 访客语言
     */
    private String viewLanguage;


    /**
     * 当前消息
     */
    @TableField(exist = false)
    private String currentMsg;


    /**
     * 当前信息发送时间
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date currentMsgSendTime;



    /**
     * 访客浏览器
     */
    private String viewBrowser;



    /**
     * 当前状态(0:排队中;1:对话中;2:已结束)
     */
    private Integer currentState;


    /**
     * 客户样式id
     */
    private Long webStyleId;

    /**
     * 访客头像
     */
    private String avatar;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;


    /**
     * 标签名，多个使用逗号隔开
     */
    @TableField(exist = false)
    private String tagNames;


    /**
     * 标签id，多个使用逗号隔开
     */
    @TableField(exist = false)
    private String tagIds;



    /**
     * 编辑客户标签关系如惨
     */
    @TableField(exist = false)
    private List<OneChatKfVisitorTagRel> tagRelList;


    /**
     * 访客消息
     */
    @TableField(exist = false)
    private List<OneChatKfVisitorMsg> visitorMsgs;


    /**
     * 消息未读数
     */
    @TableField(exist = false)
    private long notReadNumber;





}
