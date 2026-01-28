package chat.aikf.ops.service.impl;

import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.ops.api.domain.OneChatTalk;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatTalkService;
import chat.aikf.ops.mapper.OneChatTalkMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_talk(快捷话术)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatTalkServiceImpl extends ServiceImpl<OneChatTalkMapper, OneChatTalk>
    implements IOneChatTalkService {

    @Override
    public List<OneChatTalk> findList(OneChatTalk oneChatTalk) {

        List<OneChatTalk> chatTalks = this.list(new LambdaQueryWrapper<OneChatTalk>()
                .like(StringUtils.isNotEmpty(oneChatTalk.getContent()), OneChatTalk::getContent, oneChatTalk.getContent())
                        .eq(OneChatTalk::getCreateBy, SecurityUtils.getUsername())
                .orderByDesc(OneChatTalk::getCreateTime));
        return chatTalks;
    }
}




