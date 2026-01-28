package chat.aikf.ops.service.impl;

import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.ops.api.domain.OneChatWebStyle;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatWebStyleService;
import chat.aikf.ops.mapper.OneChatWebStyleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_web_style(网页接入样式)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatWebStyleServiceImpl extends ServiceImpl<OneChatWebStyleMapper, OneChatWebStyle>
    implements IOneChatWebStyleService {

    @Override
    @DataScope
    public List<OneChatWebStyle> findList(OneChatWebStyle webStyle) {
        return this.list(new LambdaQueryWrapper<OneChatWebStyle>()
                .orderByDesc(OneChatWebStyle::getCreateTime));
    }
}




