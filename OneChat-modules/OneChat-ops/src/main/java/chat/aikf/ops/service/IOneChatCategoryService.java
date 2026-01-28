package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
* @author robin
* @description 针对表【one_chat_category(分组相关)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatCategoryService extends IService<OneChatCategory> {


    /**
     * 查询列表
     * @param chatCategory
     * @return
     */
    List<OneChatCategory> findList(OneChatCategory chatCategory);

}
