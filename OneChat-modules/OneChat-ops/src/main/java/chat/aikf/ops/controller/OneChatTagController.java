package chat.aikf.ops.controller;


import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.core.web.page.TableDataInfo;
import chat.aikf.common.log.annotation.Log;
import chat.aikf.common.log.enums.BusinessType;
import chat.aikf.ops.api.domain.OneChatTagGroup;
import chat.aikf.ops.api.domain.OneChatTalk;
import chat.aikf.ops.service.IOneChatTagGroupService;
import chat.aikf.ops.service.IOneChatTagService;
import chat.aikf.ops.service.IOneChatTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 标签管理
 */
@RestController
@RequestMapping("/tag")
public class OneChatTagController extends BaseController {




    @Autowired
    private IOneChatTagGroupService oneChatTagGroupService;

    /**
     * 获取标签列表
     */
    @GetMapping("/list")
    public TableDataInfo list(OneChatTagGroup tagGroup)
    {
        startPage();

        List<OneChatTagGroup> tagGroups = oneChatTagGroupService.findList(tagGroup);

        return getDataTable(tagGroups);
    }



    /**
     * 新增标签
     */

    @Log(title = "新增标签", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody OneChatTagGroup tagGroup)
    {
        oneChatTagGroupService.saveOrUpdateTagGroup(tagGroup);
        return R.ok();
    }



    /**
     * 编辑标签
     */
    @Log(title = "编辑标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody OneChatTagGroup tagGroup)
    {

        oneChatTagGroupService.saveOrUpdateTagGroup(tagGroup);

        return R.ok();
    }

    /**
     * 删除标签组
     */
    @Log(title = "删除标签组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable String[] ids)
    {
        oneChatTagGroupService.removeTagGroup(ids);

        return R.ok();
    }




}
