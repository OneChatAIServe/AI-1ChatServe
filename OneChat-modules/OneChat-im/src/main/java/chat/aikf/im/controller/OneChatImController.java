package chat.aikf.im.controller;

import chat.aikf.common.core.domain.R;
import chat.aikf.common.mq.content.CommonMqConstants;
import chat.aikf.im.api.domain.dto.VisitorStateDto;
import chat.aikf.im.mq.sender.MqSender;
import chat.aikf.im.tio.conversation.service.VisitorStateService;
import chat.aikf.im.tio.model.BroadcastMsgDto;
import chat.aikf.im.tio.model.IdentityMsgDto;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/im")
public class OneChatImController {


    @Autowired
    private VisitorStateService visitorStateService;

    @Autowired
    private MqSender mqSender;



    /**
     * 结束会话
     * @param visitorStateDto
     * @return
     */
    @PostMapping("/endChat")
    public R endChat(@RequestBody VisitorStateDto visitorStateDto){
        visitorStateService
                .processByStateToUser(visitorStateDto.getWebStyleId(),visitorStateDto.getKfRuleId(),visitorStateDto.getVisitorId(),visitorStateDto.getUserAccount(), OneChatVisitorSate.END_STATE);
        return R.ok();
    }


    /**
     *  接入中
     * @param visitorStateDto
     * @return
     */
    @PostMapping("/accessChat")
    public R accessChat(@RequestBody  VisitorStateDto visitorStateDto){
        visitorStateService
                .processByStateToUser(visitorStateDto.getWebStyleId(),visitorStateDto.getKfRuleId(),visitorStateDto.getVisitorId(),visitorStateDto.getUserAccount(), OneChatVisitorSate.RECEIVE_STATE);
        return R.ok();


    }


    @GetMapping("/getXX")
    public R getXX(){
        try {
            mqSender.broadcastMessage(BroadcastMsgDto.builder().sendType("guest-ping").build());
//            mqSender.sendMsg(CommonMqConstants.VISITOR_PRODUCER, IdentityMsgDto.builder().build());
        }catch (Exception e){
            e.printStackTrace();
        }

        return R.ok();
    }


}
