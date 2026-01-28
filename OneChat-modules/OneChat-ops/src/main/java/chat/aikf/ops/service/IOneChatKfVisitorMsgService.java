package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_visitor_msg(访客回话表)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatKfVisitorMsgService extends IService<OneChatKfVisitorMsg> {


    /**
     * 访客消息入库
     * @param oneChatKfVisitorMsg
     */
    void addVisitorMsg(OneChatKfVisitorMsg oneChatKfVisitorMsg);


    /**
     * 获取访客信息
     * @param kfVisitorId
     * @return
     */
    List<OneChatKfVisitorMsg> getMsgList(@PathVariable Long kfVisitorId);

}
