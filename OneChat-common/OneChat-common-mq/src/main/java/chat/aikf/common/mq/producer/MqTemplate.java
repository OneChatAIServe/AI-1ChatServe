package chat.aikf.common.mq.producer;


import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通用 MQ 消息发送模板
 * 支持发送任意对象（自动 JSON 序列化）
 */
@Component
public class MqTemplate {

    private final StreamBridge streamBridge;

    public MqTemplate(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * 发送消息到指定 binding
     *
     * @param bindingName 如 "chat-out-0", "order-out-0"
     * @param message     消息体（POJO，将被序列化为 JSON）
     */
    public void send(String bindingName, Object message) {
        streamBridge.send(bindingName, message);
    }


    /**
     * 发送消息到指定 binding 支持headers
     *
     * @param bindingName 如 "chat-out-0", "order-out-0"
     * @param message     消息体（POJO，将被序列化为 JSON）
     * @param headers
     */
    public void send(String bindingName, Object message, Map<String, Object> headers) {
        MessageBuilder<Object> mb = MessageBuilder.withPayload(message);
        if (headers != null) {
            headers.forEach(mb::setHeader);
        }
        Message<Object> msg = mb.build();
        streamBridge.send(bindingName, msg);
    }
}
