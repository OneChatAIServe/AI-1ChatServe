package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatTagGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_tag_group(标签组)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatTagGroupService extends IService<OneChatTagGroup> {


    /**
     * 获取标签列表
     * @return
     */

    List<OneChatTagGroup> findList(OneChatTagGroup tagGroup);


    /**
     * 新增后更新标签
     * @param tagGroup
     */
    void saveOrUpdateTagGroup(OneChatTagGroup tagGroup);


    /**
     * 删除标签组
     * @param ids
     */
    void removeTagGroup(String[] ids);

}
