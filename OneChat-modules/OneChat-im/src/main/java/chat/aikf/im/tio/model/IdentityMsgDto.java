package chat.aikf.im.tio.model;

import chat.aikf.common.core.utils.DeviceUtils;
import chat.aikf.common.core.utils.IpLocationUtils;
import chat.aikf.common.core.utils.SnowFlakeUtils;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.im.tio.constant.OneChatChannelTypes;
import chat.aikf.im.api.constant.OneChatMsgTypes;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import cn.hutool.core.collection.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tio.http.common.HttpRequest;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentityMsgDto {
    /**
     * 当前状态(0:排队中;1:成功分配;4:客服离线)
     */
    private Integer receptionState;

    /**
     * 消息发送人
     */
    private String fromObj;


    /**
     * 消息接受人
     */
    private String toObj;


    /**
     * 消息接受人名称
     */
    private String toObjName;


    /**
     * 消息接受人头像
     */
    private String toObjavatar;



    /**
     * 访客id
     */
    private String kfVisitorId;


    /**
     * 客服规则id
     */
    private String kfRuleId;


    /**
     * 对应样式id
     */
    private String webStyleId;


    /**
     * 消息提示
     */
    private String msgTip;



    private OneChatkfVisitor visitor;


    /**
     * 当前客服规则
     */
    private OneChatKfRule oneChatKfRule;


    /**
     * 构建访客信息
     * @param httpRequest
     * @param restored
     * @param identityMsgDto
     * @param deviceInfo
     * @return
     */
    public static OneChatkfVisitor buildOneChatkfVisitorInfo(HttpRequest httpRequest, VisitorSessionKey restored, IdentityMsgDto identityMsgDto, DeviceUtils.DeviceInfo deviceInfo){

        OneChatkfVisitor oneChatkfVisitor = OneChatkfVisitor.builder()
                .name(IpLocationUtils.getCityByIp(
                        StringUtils.isEmpty(httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP))?httpRequest.getClientIp():httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP)
                )+restored.getVisitorIdLast4())
                .visitorId(restored.visitorId())
                .webStyleId(Long.parseLong(identityMsgDto.getWebStyleId()))
                .kfRuleId(Long.parseLong(identityMsgDto.getKfRuleId()))
                .userAccount(identityMsgDto.getToObj())
                .ipaddr(  StringUtils.isEmpty(httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP))?httpRequest.getClientIp():httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP))
                .ipRealAddr(IpLocationUtils.getCityByIp( StringUtils.isEmpty(httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP))?httpRequest.getClientIp():httpRequest.getHeaders().get(OneChatImConstant.X_REAL_IP)))
                .currentViewTime(new Date())
                .firstViewTime(new Date())
                .viewNumber(1)
                .channelType(OneChatChannelTypes.CHANNEL_TYPE_WEB)
                .viewDevice(deviceInfo.getDeviceType())
                .viewOs(deviceInfo.getOs())
                .viewLanguage(deviceInfo.getLanguage())
                .viewBrowser(deviceInfo.getBrowser())
                .currentState(identityMsgDto.getReceptionState())
                .build();


        oneChatkfVisitor.setVisitorMsgs(ListUtil.toList(
                OneChatKfVisitorMsg.builder()
                        .id(SnowFlakeUtils.nextId())
                        .showAvatar(identityMsgDto.getToObjavatar())
                        .showName(identityMsgDto.getToObjName())
                        .fromObj(identityMsgDto.getFromObj())
                        .toObj(identityMsgDto.getToObj())
                        .kfRuleId(Long.parseLong(identityMsgDto.getKfRuleId()))
                        .msgType(OneChatMsgTypes.MSG_TYPE_TEXT)
                        .content(identityMsgDto.getMsgTip())
                        .sendTime(new Date())
                        .build()
        ));


        return oneChatkfVisitor;

    }



}
