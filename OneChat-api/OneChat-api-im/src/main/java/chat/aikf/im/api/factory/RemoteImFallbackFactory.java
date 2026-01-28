package chat.aikf.im.api.factory;

import chat.aikf.common.core.domain.R;
import chat.aikf.im.api.RemoteImService;
import chat.aikf.im.api.domain.dto.VisitorStateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * im服务降级处理
 *
 * @author 万洽ai客服
 */
@Component
@Slf4j
public class RemoteImFallbackFactory implements FallbackFactory<RemoteImService> {
    @Override
    public RemoteImService create(Throwable cause) {
        log.error("ops服务调用失败:{}", cause.getMessage());
        return new RemoteImService()
        {

            @Override
            public R endChat(VisitorStateDto visitorStateDto, String source) {
                return R.fail("结束会话失败:" + cause.getMessage());
            }

            @Override
            public R accessChat(VisitorStateDto visitorStateDto, String source) {
                return R.fail("会话接入中失败:" + cause.getMessage());
            }
        };
    }
}
