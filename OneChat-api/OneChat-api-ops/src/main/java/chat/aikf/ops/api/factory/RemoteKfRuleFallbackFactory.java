package chat.aikf.ops.api.factory;


import chat.aikf.common.core.domain.R;
import chat.aikf.ops.api.RemoteKfRuleService;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * ops服务降级处理
 *
 * @author 万洽ai客服
 */
@Component
public class RemoteKfRuleFallbackFactory  implements FallbackFactory<RemoteKfRuleService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteKfRuleFallbackFactory.class);

    @Override
    public RemoteKfRuleService create(Throwable throwable) {
        log.error("ops服务调用失败:{}", throwable.getMessage());
        return new RemoteKfRuleService()
        {
            @Override
            public R<OneChatKfRule> findOneChatKfRule( Long id, String source)
            {
                return R.fail("获取客服组规则失败:" + throwable.getMessage());
            }

            @Override
            public R<OneChatkfVisitor> addOrUpdate(OneChatkfVisitor oneChatkfVisitor, String source) {
                return R.fail("新增或编辑访客失败:" + throwable.getMessage());
            }

            @Override
            public R<OneChatKfRule> findOneChatKfRuleByWebStyleId(Long id, String source) {
                return R.fail("通过网页样式id获取客服组规则:" + throwable.getMessage());
            }
        };
    }


}
