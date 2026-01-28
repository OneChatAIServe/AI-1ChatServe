package chat.aikf.ops.api;


import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.constant.ServiceNameConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.factory.RemoteKfRuleFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 规则接口调用
 */
@FeignClient(contextId = "remoteKfRuleService", value = ServiceNameConstants.OneChatOps, fallbackFactory = RemoteKfRuleFallbackFactory.class)
public interface RemoteKfRuleService {


    /**
     * 根据规则id获取规则详情
     * @param id
     * @param source
     * @return
     */
    @GetMapping("/kfrule/{id}")
    public R<OneChatKfRule> findOneChatKfRule(@PathVariable("id") Long id,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 新增或编辑访客
     * @param oneChatkfVisitor
     * @param source
     * @return
     */
    @PostMapping("/kfrule/addOrUpdate")
    public R<OneChatkfVisitor> addOrUpdate(@RequestBody OneChatkfVisitor oneChatkfVisitor,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 通过网页样式id获取客服组规则
     * @param id
     * @param source
     * @return
     */
    @GetMapping("/kfrule/findOneChatKfRuleByWebStyleId/{id}")
    public R<OneChatKfRule> findOneChatKfRuleByWebStyleId(@PathVariable("id")Long id,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
