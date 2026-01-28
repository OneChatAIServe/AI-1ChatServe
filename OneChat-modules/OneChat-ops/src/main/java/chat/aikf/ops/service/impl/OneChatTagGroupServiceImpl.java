package chat.aikf.ops.service.impl;

import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.ops.api.domain.OneChatTag;
import chat.aikf.ops.api.domain.OneChatTagGroup;
import chat.aikf.ops.service.IOneChatTagService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import chat.aikf.ops.service.IOneChatTagGroupService;
import chat.aikf.ops.mapper.OneChatTagGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author robin
* @description 针对表【one_chat_tag_group(标签组)】的数据库操作Service实现
* @createDate 2025-12-12 11:04:32
*/
@Service
public class OneChatTagGroupServiceImpl extends ServiceImpl<OneChatTagGroupMapper, OneChatTagGroup>
    implements IOneChatTagGroupService {

    @Autowired
    private IOneChatTagService oneChatTagService;


    @Override
    @DataScope
    public List<OneChatTagGroup> findList(OneChatTagGroup tagGroup) {
        List<OneChatTagGroup> tagGroups = this.list(new LambdaQueryWrapper<OneChatTagGroup>()
                .like(StringUtils.isNotEmpty(tagGroup.getTagGroupName())
                        , OneChatTagGroup::getTagGroupName, tagGroup.getTagGroupName()));

        if(CollectionUtil.isNotEmpty(tagGroups)){

            List<OneChatTag> oneChatTags = oneChatTagService.list(new LambdaQueryWrapper<OneChatTag>()
                    .in(OneChatTag::getTagGroupId, tagGroups.stream().map(OneChatTagGroup::getId).
                            collect(Collectors.toList())));

            if(CollectionUtil.isNotEmpty(oneChatTags)){
                tagGroups.stream().forEach(item->{
                    item.setChatTagList(
                            oneChatTags.stream().filter(tag->tag.getTagGroupId().equals(item.getId())).collect(Collectors.toList())
                    );
                });

            }



        }

        return tagGroups;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateTagGroup(OneChatTagGroup tagGroup) {
        if(saveOrUpdate(tagGroup)){
            List<OneChatTag> chatTagList = tagGroup.getChatTagList();
            if(CollectionUtil.isNotEmpty(chatTagList)){
                List<Long> idList = chatTagList.stream()
                        .filter(Objects::nonNull)                     // 过滤掉 null 元素
                        .peek(item -> item.setTagGroupId(tagGroup.getId())) // 赋值 groupId
                        .filter(item -> item.getId() != null)         // 只保留 id 非 null 的项
                        .map(OneChatTag::getId)
                        .collect(Collectors.toList());
                oneChatTagService.remove(new LambdaQueryWrapper<OneChatTag>()
                                .eq(OneChatTag::getTagGroupId,tagGroup.getId())
                        .notIn(CollectionUtil.isNotEmpty(idList),OneChatTag::getId,idList));

                oneChatTagService.saveOrUpdateBatch(chatTagList);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagGroup(String[] ids) {
       if(this.removeByIds(ListUtil.toList(ids))){
           oneChatTagService.remove(new LambdaQueryWrapper<OneChatTag>()
                   .in(OneChatTag::getTagGroupId,ListUtil.toList(ids)));
       }

    }
}




