package chat.aikf.im.mq.sender;



import chat.aikf.common.mq.content.CommonMqConstants;
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


    /**
     * 发送广播通知所有im节点
     * @param message
     */
    public void broadcastMessage(Object message){
        mqTemplate.send(CommonMqConstants.TIO_COLONY_TOPIC, message);
    }




}