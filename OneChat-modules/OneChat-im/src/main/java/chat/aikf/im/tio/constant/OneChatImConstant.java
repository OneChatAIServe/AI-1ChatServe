package chat.aikf.im.tio.constant;


/**
 * @author tanyaowu
 *
 */
public abstract class OneChatImConstant {


    /**
     * 编码
      */
    public static final String CHARSET = "utf-8";


    /**
     * 用于群聊的group id
     */
    public static final String GROUP_ID = "showcase-websocket";


    /**
     * 客户端类型标识
     */
    public static final String CLIENT_TYPE="clientType";


    /**
     * 客户端类型标识-访客
     */
    public static final String CLIENT_TYPE_GUEST="guest";


    /**
     * tio的user_id
     */
    public static final String TIO_USER_ID="tio_user_id";



    /**
     * 客户端类型标识-员工
     */
    public static final String CLIENT_TYPE_USER="user";



    /**
     * tio的User_Agent标识
     */
    public static final String TIO_USER_AGENT="user-agent";


    /**
     * tio的accept-language标识
     */
    public static final String TIO_ACCEPT_LANGUAGE="accept-language";


    /**
     * ip标识请求头
     */
    public static final String X_REAL_IP="x-real-ip";


    /**
     * 访客端ping标识
     */
    public static final String GUEST_TIO_PING="guest-ping";



    /**
     * 管理端ping标识
     */
    public static final String USER_TIO_PING="user-ping";


    /**
     * 发送消息标识
     */
    public static final String SEND_MSG="sendMsg";


    /**
     * 通知用户
     */
    public static final String NOTIFY_USER="notifyUser";


    /**
     * 访客session响应
     */
    public static final String SESSION_RESP="sessionResp";

    /**
     * 消息来源，访客
     */
    public static final Integer GUEST_MSGSOURCE=0;


    /**
     * 消息来源,员工
     */
    public static final Integer USER_MSGSOURCE=1;
}

