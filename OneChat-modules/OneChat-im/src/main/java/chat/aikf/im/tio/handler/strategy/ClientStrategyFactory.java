package chat.aikf.im.tio.handler.strategy;

import chat.aikf.im.tio.constant.OneChatImConstant;
import org.springframework.stereotype.Component;

@Component
public class ClientStrategyFactory {
    private final GuestClientStrategy guestClientStrategy = new GuestClientStrategy();
    private final UserClientStrategy userStrategy = new UserClientStrategy();


    public ClientStrategy getStrategy(String clientType) {
        switch (clientType) {
            case OneChatImConstant.CLIENT_TYPE_GUEST:
                return guestClientStrategy;
            case OneChatImConstant.CLIENT_TYPE_USER:
                return userStrategy;
            default:
                throw new IllegalArgumentException("不支持的客户端类型: " + clientType);
        }
    }
}
