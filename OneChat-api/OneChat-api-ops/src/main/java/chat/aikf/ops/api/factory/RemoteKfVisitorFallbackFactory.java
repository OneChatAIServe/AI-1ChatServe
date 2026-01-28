package chat.aikf.ops.api.factory;

import chat.aikf.common.core.domain.R;
import chat.aikf.ops.api.RemoteKfVisitorService;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.domain.dto.OneChatStateCountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * ops服务降级处理
 *
 * @author 万洽ai客服
 */
@Component
@Slf4j
public class RemoteKfVisitorFallbackFactory implements FallbackFactory<RemoteKfVisitorService> {
    @Override
    public RemoteKfVisitorService create(Throwable cause) {
        log.error("ops服务调用失败:{}", cause.getMessage());
        return new RemoteKfVisitorService()
        {
            @Override
            public R<OneChatkfVisitor> addOrUpdate(OneChatkfVisitor oneChatkfVisitor, String source) {
                return R.fail("新增或编辑访客失败:" + cause.getMessage());
            }

            @Override
            public R<OneChatKfVisitorMsg> addMsgVisitor(OneChatKfVisitorMsg kfVisitorMsg, String source) {
                return R.fail("访客消息入库失败:" + cause.getMessage());
            }

            @Override
            public R<OneChatStateCountDto> getIdleStateCount(String kfRuleId, String source) {
                return R.fail("排队中的访客数量失败:" + cause.getMessage());
            }

            @Override
            public R<OneChatkfVisitor> findAccessVisitor(String source) {
                return R.fail("获取一个最久接入排队中的访客失败:" + cause.getMessage());
            }

            @Override
            public R<OneChatkfVisitor> findAppointVisitor(OneChatkfVisitor oneChatkfVisitor, String source) {
                return R.fail("根据指定条件获取访客(一个)失败:" + cause.getMessage());
            }

            @Override
            public R<List<OneChatkfVisitor>> findAppointVisitorList(OneChatkfVisitor oneChatkfVisitor, String source) {
                return R.fail("根据指定条件获取访客(多个)失败:" + cause.getMessage());
            }
        };
    }
}
