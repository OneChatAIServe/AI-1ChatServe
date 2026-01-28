package chat.aikf.im.tio.conversation.state;

import chat.aikf.common.core.utils.DeviceUtils;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.im.allocation.service.CustomerAssignService;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.constant.OneChatImConstant;
import chat.aikf.im.tio.model.IdentityMsgDto;
import chat.aikf.im.tio.model.VisitorSessionKey;
import chat.aikf.im.tio.service.ChatMessageService;
import chat.aikf.im.tio.starter.OneChatImStarter;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;

/**
 * 访客接入逻辑
 */
@Slf4j
@Component
public class IdleState implements VisitorState {


    @Autowired
    private CustomerAssignService customerAssignService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private MqSender mqSender;


    @Autowired
    private OneChatImStarter oneChatImStarter;



    @Override
    public void handleToGuest(HttpRequest httpRequest, VisitorSessionKey restored) {

        //分配接待员工给客户(分配逻辑)
        IdentityMsgDto identityMsgDto = customerAssignService.
                getOnlineUserId( restored.webStyleId(), restored.visitorId());
        log.info("接待人:"+identityMsgDto);
        //访客消息入库处理
        if(null != identityMsgDto){
            //构建访客信息(如果或更新+构建缓存链接关系)
            OneChatkfVisitor oneChatkfVisitor =  IdentityMsgDto.buildOneChatkfVisitorInfo(httpRequest,restored,identityMsgDto, DeviceUtils.parseDeviceWithLanguage(httpRequest
                    .getHeader(OneChatImConstant.TIO_USER_AGENT),httpRequest
                    .getHeader(OneChatImConstant.TIO_ACCEPT_LANGUAGE)));
            identityMsgDto.setVisitor(oneChatkfVisitor);
            try {
                // 尝试发 MQ
                mqSender.sendMsg(CommonMqConstants.VISITOR_PRODUCER,identityMsgDto);
            } catch (Exception mqEx) {
                log.warn("MQ 发送失败，visitorUserId={}，降级落库", oneChatkfVisitor.getVisitorId());
                // 降级：直接调用落库处理
                chatMessageService.handleVisitorInfo(identityMsgDto);
            }
        }else{ //初始化分配失败,关闭连接


            Tio.closeUser(oneChatImStarter.getServerTioConfig(),restored.toString(),"初始化异常分配失败");


        }
    }

    @Override
    public void handleToUser(String kfRuleId, String visitorId, String userAccount, String webStyleId) {

    }




}
