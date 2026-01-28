package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatKfVisitorTagRel;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_kf_visitor_tag_rel(访客标签关系表)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatKfVisitorTagRelService extends IService<OneChatKfVisitorTagRel> {


    /**
     * 访客打标签
     * @param oneChatkfVisitor
     */
    List<OneChatKfVisitorTagRel> makeTag(OneChatkfVisitor oneChatkfVisitor);

}
