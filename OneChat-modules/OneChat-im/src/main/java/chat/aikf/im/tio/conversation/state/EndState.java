package chat.aikf.im.tio.conversation.state;


import chat.aikf.im.tio.model.VisitorSessionKey;
import chat.aikf.im.tio.utils.KfCacheRelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.http.common.HttpRequest;

/**
 * 结束状态动作
 */
@Component
@Slf4j
public class EndState implements VisitorState {




    @Autowired
    private KfCacheRelUtils kfCacheRelUtils;




    @Override
    public void handleToGuest(HttpRequest httpRequest, VisitorSessionKey restored) {

    }

    @Override
    public void handleToUser(String kfRuleId, String visitorId, String userAccount,String webStyleId) {


        kfCacheRelUtils.removeLinkInitCache(kfRuleId,userAccount,visitorId,webStyleId);

    }

}