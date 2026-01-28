package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatTalk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_talk(快捷话术)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatTalkService extends IService<OneChatTalk> {


    /**
     * 获取话术列表
     * @param oneChatTalk
     * @return
     */
    List<OneChatTalk> findList(OneChatTalk oneChatTalk);

}
