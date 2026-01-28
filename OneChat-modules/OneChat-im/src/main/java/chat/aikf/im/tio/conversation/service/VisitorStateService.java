package chat.aikf.im.tio.conversation.service;



import chat.aikf.common.core.utils.SpringUtils;
import chat.aikf.im.tio.conversation.state.EndState;
import chat.aikf.im.tio.conversation.state.IdleState;
import chat.aikf.im.tio.conversation.state.ReceiveState;
import chat.aikf.im.tio.conversation.state.VisitorState;
import chat.aikf.im.tio.model.VisitorSessionKey;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.http.common.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Slf4j
@Service
public class VisitorStateService {



    private static final Map<Integer, VisitorState> STATE_HANDLER_MAP=new ConcurrentHashMap<>();


    static {

        STATE_HANDLER_MAP.put(OneChatVisitorSate.IDLE_STATE, SpringUtils.getBean(IdleState.class));
        STATE_HANDLER_MAP.put(OneChatVisitorSate.RECEIVE_STATE, SpringUtils.getBean(ReceiveState.class));
        STATE_HANDLER_MAP.put(OneChatVisitorSate.END_STATE, SpringUtils.getBean(EndState.class));

    }




    /**
     * 状态处理(面向访客)
     */
    public void processByStateToGuest(HttpRequest request, VisitorSessionKey sessionKey, Integer currentState) {


        if (currentState != null) {
            VisitorState visitorState = STATE_HANDLER_MAP.get(currentState);
            if(null != visitorState){
                try {
                    visitorState.handleToGuest(request, sessionKey);
                } catch (Exception e) {
                    log.error("处理访客状态 {} 时发生异常, visitorId={}", currentState, sessionKey.visitorId(), e);
                    throw new RuntimeException("访客状态处理失败", e);
                }
            }
        }

    }


    /**
     * 状态处理(面向客服)
     * @param webStyleId
     * @param kfRuleId
     * @param visitorId
     * @param userAccount
     * @param currentState
     */
    public void processByStateToUser(String webStyleId,String kfRuleId, String visitorId,String userAccount, Integer currentState) {


        if(null != currentState){
            VisitorState visitorState = STATE_HANDLER_MAP.get(currentState);
            if(null != visitorState){
                try {
                    visitorState.handleToUser(kfRuleId, visitorId,userAccount,webStyleId);
                } catch (Exception e) {
                    log.error("客服端手动处理状态 {} 时发生异常", currentState, e);
                    throw new RuntimeException("访客状态处理失败", e);
                }
            }
        }


    }
}