package chat.aikf.im.mq.listener;

import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.im.tio.model.IdentityMsgDto;
import chat.aikf.im.tio.model.OneChatMsgDto;
import chat.aikf.im.tio.service.ChatMessageService;
import chat.aikf.ops.api.RemoteKfRuleService;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

/**
 * 聊天消息监听器（函数式模型）
 * 方法名: chatMessageConsumer → 自动绑定到 chatMessageConsumer-in-0
 * 对应 RabbitMQ 队列: im.chat.p2p.im-chat-consumer-group
 */
@Component
@Slf4j
public class ChatMessageListener {



    @Autowired
    private ChatMessageService chatMessageService;



    /**
     * 访客消息处理
     * @return
     */
    @Bean
    public Consumer<OneChatMsgDto> chatMessageConsumer() {
        return message -> {
            try {
                log.info("【IM】开始处理访客处理消息: {}", message);
                chatMessageService.savePendingMessage(message);

            } catch (Exception e) {
                log.error("处理消息失败，将触发重试或进入 DLQ: {}", message, e);
                throw new RuntimeException("消息处理异常", e); // 触发 SCSt 重试机制
            }
        };
    }



    /**
     * 访客信息更新入库
     */
    @Bean
    public Consumer<IdentityMsgDto> visitorConsumer() {
        return message -> {
            try {
                log.error("访客初始化失败, IdentityMsgDto={}", message);
                chatMessageService.handleVisitorInfo(message);

            } catch (Exception e) {
                log.error("访客初始化失败, IdentityMsgDto={}", message, e);
                throw new RuntimeException("访客初始化异常", e); // 触发重试 → DLQ
            }
        };
    }

}