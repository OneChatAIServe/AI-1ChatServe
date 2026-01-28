package chat.aikf.ops.api.constant;


/**
 * 访客状态
 */
public class OneChatVisitorSate {
    //排队中
    public static final Integer IDLE_STATE=0;

    //接待中
    public static final Integer RECEIVE_STATE=1;

    //会话结束
    public static final Integer END_STATE=2;

    //客服离线会话结束
    public static final Integer OFFLINE_STATE=4;
}
