package chat.aikf.ai.controller;

import chat.aikf.ai.domain.VisitorMessageDto;
import chat.aikf.ai.service.OneChatAstService;
import chat.aikf.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * AI会话
 */
@RestController
@RequestMapping("/ast")
@Slf4j
public class OneChatAstController {

    @Autowired
    private OneChatAstService oneChatAstService;


    /**
     * 推荐回复
     * @param request
     * @return
     */
    @PostMapping("/recommendReply")
    public R<String> recommendReply(@RequestBody VisitorMessageDto request) {

        try {
            String reply = oneChatAstService.recommendReply(request.getVisitorMessage());
            return R.ok(reply);
        } catch (RuntimeException e) {
            log.error("生成失败", e);
            return R.fail(e.getMessage());
        }
    }

}
