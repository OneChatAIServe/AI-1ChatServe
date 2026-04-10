package chat.aikf.im.tio.model;

import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastMsgDto implements Serializable {
    //发送类型  访客端心跳:guest-ping 管理端心跳:admin-ping  发送消息:sendMsg 通知:notifyUser 访客初始化请求数据响应:sessionResp
    private String sendType;

    private String toUserId;
    //发送信息需要 sendMsg
    private OneChatKfVisitorMsg oneChatKfVisitorMsg;
    private OneChatMsgDto chatMsgDto;

    //通知 notifyUser
    private IdentityMsgDto msgDto;

    //访客初始化请求数据响应:sessionResp
    private GuestIdentityMsgDto identityMsgDto;


    /**
     * 设置toUserId
     * @param msgDto
     * @return
     */
    public static BroadcastMsgDto restartToUserId(BroadcastMsgDto msgDto){

        if(OneChatImConstant.GUEST_TIO_PING.equals(msgDto.getSendType())||
                OneChatImConstant.USER_TIO_PING.equals(msgDto.getSendType())||
                OneChatImConstant.SESSION_RESP.equals(msgDto.getSendType())
        ){
            msgDto.setToUserId(msgDto.getToUserId());
        }

        if(OneChatImConstant.NOTIFY_USER.equals(msgDto.getSendType())
        ){
            msgDto.setToUserId(msgDto.getMsgDto().getToObj());
        }

        if( OneChatImConstant.SEND_MSG.equals(msgDto.getSendType())){
            msgDto.setToUserId(msgDto.getChatMsgDto().getToObj());
        }
        return msgDto;
    }
}
