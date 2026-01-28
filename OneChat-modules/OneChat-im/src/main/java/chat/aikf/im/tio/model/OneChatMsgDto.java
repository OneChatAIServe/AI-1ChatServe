package chat.aikf.im.tio.model;

import chat.aikf.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatMsgDto {
    /**
     * 消息发送人
     */
    private String fromObj;


    /**
     * 展示名称
     */
    private String showName;


    /**
     * 展示头像
     */
    private String showAvatar;


    /**
     * 消息状态字段(主要是面向管理端发消息的 1:正常对话中;2:接入中发送的消息;3:已结束发送的消息)
     */
    private Integer msgStatus;




    /**
     * 消息接受人
     */
    private String toObj;

    /**
     * 消息类型 text文字  emotion表情  image图片
     */
    private String msgType;


    /**
     * 接受内容
     */
    private String content;


    /**
     * 消息标识
     */
    private String msgId;

    /**
     * 消息来源 0:访客 1:员工客服
     */
    private Integer msgSource;

    /**
     * 是否已读:0:未读;1:已读
     */
    private Integer readReceipt;

    /**
     * 访客id
     */
    private Long kfVisitorId;


    /**
     * 客服组规则id
     */
    private Long kfRuleId;


    /**
     * 客服样式id
     */
    private Long webStyleId;

    /**
     * 发送时间
     */
    private Date sendTime;


    private String clientType;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 定义 OneChatMsgDto 必需的字段列表
    private static final List<String> REQUIRED_FIELDS = Arrays.asList("fromObj", "msgType", "toObj", "content","kfVisitorId");


    /**
     * 综合校验：1. 是否为有效JSON 2. 是否包含必需字段
     * @paramString 待校验的JSON字符串
     * @return 如果字符串是有效JSON且包含所有必需字段，则返回 true
     */
    public static boolean isValidOneChatMsgDto(String jsonString) {
        // 1. 检查字符串是否为空
        if (StringUtils.isEmpty(jsonString)) {
            return false;
        }

        // 2. 检查是否为有效的JSON格式（例如，检查是否以 { 开头，以 } 结尾）
        String trimmedJson = jsonString.trim();
        if (!trimmedJson.startsWith("{") || !trimmedJson.endsWith("}")) {
            return false;
        }

        // 3. 使用Jackson尝试解析并验证结构
        try {
            JsonNode jsonNode = objectMapper.readTree(trimmedJson);

            // 检查JSON中是否包含所有必需的字段
            for (String field : REQUIRED_FIELDS) {
                if (!jsonNode.has(field)) {
                    // 缺少必需字段
                    return false;
                }
            }
            // 所有检查通过
            return true;

        } catch (JsonProcessingException e) {
            // JSON格式无效（如语法错误）
            return false;
        } catch (Exception e) {
            // 其他解析异常
            return false;
        }
    }


}
