package chat.aikf.im.tio.model;


import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityMsgDto {

    /**
     * 系统状态(0:排队中;1:对话中;2:已结束;3:心跳回应)
     * 访客端状态:(0:排队中(初始化状态);1:对话中(初始化状态);2:链接成功正常对话(除结束语);3:心跳状态;4:客服结束语)
     */
    private Integer initState;


    /**
     * 消息
     */
    private OneChatKfVisitorMsg visitorMsg;





}
