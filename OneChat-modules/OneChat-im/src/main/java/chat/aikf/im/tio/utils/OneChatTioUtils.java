package chat.aikf.im.tio.utils;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.utils.lock.SetWithLock;

public class OneChatTioUtils {


    /**
     * 判断使用是否存在tio当前节点
     * @param tioConfig
     * @param userid
     * @return
     */
    public static boolean localUserIdExist(TioConfig tioConfig, String userid) {

        SetWithLock<ChannelContext> userChannels = Tio.getByUserid(tioConfig, userid);
        if (userChannels != null && !userChannels.getObj().isEmpty()) {

              return true;
        }
        return false;
    }
}
