package chat.aikf.ops.service.impl;

import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.ops.api.domain.OneChatCategory;
import chat.aikf.ops.api.domain.OneChatTalk;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatCategoryService;
import chat.aikf.ops.mapper.OneChatCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_category(分组相关)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatCategoryServiceImpl extends ServiceImpl<OneChatCategoryMapper, OneChatCategory>
    implements IOneChatCategoryService {

    @Override
    @DataScope
    public List<OneChatCategory> findList(OneChatCategory chatCategory) {
        List<OneChatCategory> chatCategorys = this.list(new LambdaQueryWrapper<OneChatCategory>()
                .like(chatCategory.getType() !=null, OneChatCategory::getType, chatCategory.getType())
                .orderByDesc(OneChatCategory::getCreateTime));
        return chatCategorys;
    }
}




