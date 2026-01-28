package chat.aikf.ops.service.impl;

import chat.aikf.ops.api.domain.OneChatKfVisitorTagRel;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatKfVisitorTagRelService;
import chat.aikf.ops.mapper.OneChatKfVisitorTagRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author robin
* @description 针对表【one_chat_kf_visitor_tag_rel(访客标签关系表)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatKfVisitorTagRelServiceImpl extends ServiceImpl<OneChatKfVisitorTagRelMapper, OneChatKfVisitorTagRel>
    implements IOneChatKfVisitorTagRelService {



    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OneChatKfVisitorTagRel> makeTag(OneChatkfVisitor oneChatkfVisitor) {
        List<OneChatKfVisitorTagRel> tagRelList =
                oneChatkfVisitor.getTagRelList();
        if(CollectionUtil.isNotEmpty(tagRelList)){

            List<Long> tagRelIds = tagRelList.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.getId() != null)
                    .map(OneChatKfVisitorTagRel::getId)
                    .collect(Collectors.toList());

            this.remove(new LambdaQueryWrapper<OneChatKfVisitorTagRel>()
                    .eq(OneChatKfVisitorTagRel::getKfVisitorId,oneChatkfVisitor.getId())
                    .notIn(CollectionUtil.isNotEmpty(tagRelIds),OneChatKfVisitorTagRel::getId,tagRelIds));


            this.saveOrUpdateBatch(tagRelList);


        }else{ //标签为空则为取消

            this.remove(new LambdaQueryWrapper<OneChatKfVisitorTagRel>()
                    .eq(OneChatKfVisitorTagRel::getKfVisitorId,oneChatkfVisitor.getId()));

        }

        return tagRelList;

    }
}




