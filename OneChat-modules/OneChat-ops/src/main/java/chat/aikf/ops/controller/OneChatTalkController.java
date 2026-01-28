package chat.aikf.ops.controller;


import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.core.web.page.TableDataInfo;
import chat.aikf.common.log.annotation.Log;
import chat.aikf.common.log.enums.BusinessType;
import chat.aikf.ops.api.domain.OneChatTalk;
import chat.aikf.ops.service.IOneChatTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * 客服话术
 */
@RestController
@RequestMapping("/talk")
public class OneChatTalkController extends BaseController {


    @Autowired
    private IOneChatTalkService iOneChatTalkService;

    /**
     * 获取话术列表
     */
    @GetMapping("/list")
    public TableDataInfo list(OneChatTalk oneChatTalk)
    {
        startPage();

        List<OneChatTalk> chatTalks = iOneChatTalkService.findList(oneChatTalk);

        return getDataTable(chatTalks);
    }



    /**
     * 新增话术
     */

    @Log(title = "新增话术", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody OneChatTalk oneChatTalk)
    {
        iOneChatTalkService.save(oneChatTalk);
        return R.ok();
    }



    /**
     * 编辑话术
     */
    @Log(title = "编辑话术", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody OneChatTalk oneChatTalk)
    {

        iOneChatTalkService.updateById(oneChatTalk);

        return R.ok();
    }

    /**
     * 删除话术
     */
    @Log(title = "删除话术", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R remove(@PathVariable Long id)
    {
        iOneChatTalkService.removeById(id);

        return R.ok();
    }




}
