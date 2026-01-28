package chat.aikf.im.mq.sender;



import chat.aikf.common.mq.producer.MqTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqSender {

    @Autowired
    private MqTemplate mqTemplate;

    /**
     * 发送消息到队列中
     */
    public void sendMsg(String bindingName, Object message) {

        mqTemplate.send(bindingName, message);
    }


    /**
     * 发送消息到队列中 支持headers
     * @param bindingName
     * @param message
     * @param headers
     */
    public void sendMsg(String bindingName, Object message, java.util.Map<String, Object> headers) {
        mqTemplate.send(bindingName, message, headers);
    }
}