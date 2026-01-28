package chat.aikf.im.controller;

import chat.aikf.common.core.domain.R;
import chat.aikf.im.api.domain.dto.VisitorStateDto;
import chat.aikf.im.tio.conversation.service.VisitorStateService;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/im")
public class OneChatImController {


    @Autowired
    private VisitorStateService visitorStateService;



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


}
