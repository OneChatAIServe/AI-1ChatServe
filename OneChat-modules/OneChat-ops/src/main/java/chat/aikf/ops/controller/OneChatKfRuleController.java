package chat.aikf.ops.controller;


import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.exception.ServiceException;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.log.annotation.Log;
import chat.aikf.common.log.enums.BusinessType;
import chat.aikf.ops.api.domain.OneChatKfRule;
import chat.aikf.ops.domain.OneChatKfRuleInitVo;
import chat.aikf.ops.domain.OneChatKfRuleVo;
import chat.aikf.ops.service.IOneChatKfRuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 客服管理
 */
@RestController
@RequestMapping("/kfrule")
public class OneChatKfRuleController extends BaseController {

    @Autowired
    private IOneChatKfRuleService oneChatKfRuleService;









    /**
     * 客服组列表
     * @return
     */
    @GetMapping
    public R<List<OneChatKfRuleVo>> findList(){

        List<OneChatKfRuleVo> ruleVos = oneChatKfRuleService
                .findList(new OneChatKfRule());

        return R.ok(ruleVos);
    }


    /**
     * 获取当前客服信息
     * @return
     */
    @GetMapping("/findCurrentKfInfo")
    public R<OneChatKfRuleInitVo> findCurrentKfInfo(){

        OneChatKfRuleInitVo currentKfInfo = oneChatKfRuleService.findCurrentKfInfo();
        if(null == currentKfInfo){
            return R.ok();
        }

        return R.ok(currentKfInfo);
    }









    /**
     * 新增客服组
     * @param oneChatKfRule
     * @return
     */
    @Log(title = "新增客服组", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody OneChatKfRule oneChatKfRule){

       if(oneChatKfRuleService.count(new LambdaQueryWrapper<OneChatKfRule>()
               .eq(OneChatKfRule::getRuleName,oneChatKfRule.getRuleName()))>0){
           return R.fail("客服组名称已存在");

       }

       oneChatKfRuleService
                .save(oneChatKfRule);
        return R.ok();
    }


    /**
     * 编辑客服组
     * @param oneChatKfRule
     * @return
     */
    @Log(title = "编辑客服组", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody OneChatKfRule oneChatKfRule){

        if(oneChatKfRuleService.count(new LambdaQueryWrapper<OneChatKfRule>()
                .eq(OneChatKfRule::getRuleName,oneChatKfRule.getRuleName())
                .ne(OneChatKfRule::getId,oneChatKfRule.getId()))>0){
            return R.fail("客服组名称已存在");

        }

        oneChatKfRuleService.updateOneChatKfRule(oneChatKfRule);


        return R.ok();
    }


    /**
     *  获取客服组规则明细
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<OneChatKfRule> findOneChatKfRule(@PathVariable Long id){
        OneChatKfRule oneChatKfRule = oneChatKfRuleService.findOneChatKfRule(id);

        return R.ok(oneChatKfRule);
    }



    /**
     *  通过网页样式id获取客服组规则
     * @param id
     * @return
     */
    @GetMapping("/findOneChatKfRuleByWebStyleId/{id}")
    public R<OneChatKfRule> findOneChatKfRuleByWebStyleId(@PathVariable Long id){
        OneChatKfRule oneChatKfRule = oneChatKfRuleService.findOneChatKfRuleByWebStyleId(id);

        return R.ok(oneChatKfRule);
    }


    /**
     * 删除客服规则
     */
    @Log(title = "删除客服规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable String[] ids)
    {

        try {
            oneChatKfRuleService.removeOneChatKfRule(ids);
        }catch (ServiceException e){
            return R.fail(e.getMessage());
        }
        return R.ok();
    }


}
