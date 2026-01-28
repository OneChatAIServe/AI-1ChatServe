package chat.aikf.ops.controller;

import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.log.annotation.Log;
import chat.aikf.common.log.enums.BusinessType;
import chat.aikf.ops.api.domain.OneChatWebStyle;
import chat.aikf.ops.service.IOneChatWebStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;


/**
 * 网页接入
 */
@RestController
@RequestMapping("/webStyle")
public class OneChatWebStyleController extends BaseController {

    @Autowired
    private IOneChatWebStyleService oneChatWebStyleService;




    /**
     * 客服组列表
     * @return
     */
    @GetMapping
    public R<List<OneChatWebStyle>> findList(){

        List<OneChatWebStyle> webStyles = oneChatWebStyleService
                .findList(new OneChatWebStyle());

        return R.ok(webStyles);
    }




    /**
     * 新增网页接入
     * @param oneChatWebStyle
     * @return
     */
    @Log(title = "新增网页接入", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody OneChatWebStyle oneChatWebStyle){

        oneChatWebStyleService
                .save(oneChatWebStyle);
        return R.ok();
    }


    /**
     * 编辑网页接入
     * @param oneChatWebStyle
     * @return
     */
    @Log(title = "编辑网页接入", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody OneChatWebStyle oneChatWebStyle){

        oneChatWebStyleService.updateById(oneChatWebStyle);


        return R.ok();
    }


    /**
     * 删除网页接入
     */
    @Log(title = "删除网页接入", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable String[] ids)
    {
        oneChatWebStyleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }





}
