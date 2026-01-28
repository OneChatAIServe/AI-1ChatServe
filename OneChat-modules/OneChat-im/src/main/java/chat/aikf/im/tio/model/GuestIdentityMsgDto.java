package chat.aikf.im.tio.model;

import chat.aikf.common.core.utils.SnowFlakeUtils;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestIdentityMsgDto {
    /**
     * 系统状态(0:排队中;1:成功分配;2:客服离线；3:心跳回应;4:当前会话已结束)
     */
    private Integer initState;


    /**
     * 接待人的id
     */
    private String receptObjId;


    /**
     * 接待人名称
     */
    private String receptObjName;



    /**
     * 接待人头像
     */
    private String receptObjavatar;


    /**
     * 会话时长:分钟。超过默认超过30分钟访客接入会重新初始化分配(以及访客端聊天内容存储时长)
     */
    public Integer sessionTime=30;


    /**
     * 当前访客id
     */
    private String ownObjId;

    /**
     * 当前访客名称
     */
    private String kfVisitorName;



    /**
     * 当前访客头像
     */
    private String kfVisitoravatar;



    /**
     * 访客id
     */
    private String kfVisitorId;


    /**
     * 访客唯一标识
     */
    private String visitorId;


    /**
     * 客服规则id
     */
    private String kfRuleId;


    /**
     * 消息提示
     */
    private String msgTip;


    /**
     * 消息内容
     */
    private  OneChatKfVisitorMsg visitorMsg;


    public static GuestIdentityMsgDto buildObj(IdentityMsgDto identityMsgDto, OneChatKfVisitorMsg visitorMsg, OneChatkfVisitor visitor, Integer sessionTime){
        GuestIdentityMsgDto msgDto=new GuestIdentityMsgDto();
        msgDto.setInitState(identityMsgDto.getReceptionState());
        msgDto.setVisitorId(visitor.getVisitorId());
        msgDto.setReceptObjId(identityMsgDto.getToObj());
        msgDto.setReceptObjName(identityMsgDto.getToObjName());
        msgDto.setReceptObjavatar(identityMsgDto.getToObjavatar());
        msgDto.setKfVisitorName(visitor.getName());
        msgDto.setKfVisitoravatar(visitor.getAvatar());
        msgDto.setKfVisitorId(identityMsgDto.getKfVisitorId());
        msgDto.setKfRuleId(identityMsgDto.getKfRuleId());
        msgDto.setMsgTip(identityMsgDto.getMsgTip());
        msgDto.setSessionTime(sessionTime);
        msgDto.setVisitorMsg(visitorMsg);
        return msgDto;
    }

}
