package chat.aikf.im.tio.conversation.state;


import chat.aikf.im.tio.model.VisitorSessionKey;
import org.tio.http.common.HttpRequest;


//根据访客状态处理不同的动作
public interface VisitorState {

;

    /**
     * 访客状态变更
     * @param httpRequest
     * @param restored
     */
    void handleToGuest(HttpRequest httpRequest, VisitorSessionKey restored) ;


    /**
     * 客服手动处理访客状态
     * @param kfRuleId
     * @param visitorId
     * @param userAccount
     * @param webStyleId
     */
     void handleToUser(String kfRuleId, String visitorId,String userAccount,String webStyleId) ;
}
