package chat.aikf.ops.controller;

import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.core.web.page.TableDataInfo;
import chat.aikf.common.security.utils.SecurityUtils;
import chat.aikf.im.api.RemoteImService;
import chat.aikf.im.api.domain.dto.VisitorStateDto;
import chat.aikf.ops.api.constant.OneChatReadMsgState;
import chat.aikf.ops.api.constant.OneChatVisitorSate;
import chat.aikf.ops.api.domain.OneChatKfRuleScope;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatKfVisitorTagRel;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.domain.dto.OneChatStateCountDto;
import chat.aikf.ops.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 访客管理
 */
@RestController
@RequestMapping("/kfVisitor")
public class OneChatkfVisitorController extends BaseController {


    @Autowired
    private IOneChatkfVisitorService oneChatkfVisitorService;


    @Autowired
    private IOneChatKfVisitorTagRelService oneChatKfVisitorTagRelService;


    @Autowired
    private IOneChatKfVisitorMsgService oneChatKfVisitorMsgService;




    @Autowired
    private IOneChatKfRuleScopeService iOneChatKfRuleScopeService;




    @Autowired
    private RemoteImService remoteImService;





    /**
     * 获取访客列表
     * @param oneChatkfVisitor
     * @return
     */
    @GetMapping("/findList")
    public TableDataInfo findList(OneChatkfVisitor oneChatkfVisitor){
        startPage();
        List<OneChatkfVisitor> visitors =
                oneChatkfVisitorService.findList(oneChatkfVisitor);
        return getDataTable(visitors);
    }


    /**
     * 获取对话中的访客列表(不分页)
     * @return
     */
    @GetMapping("/findVisitorToDialogue")
    public R<List<OneChatkfVisitor>> findVisitorToDialogue(){
        List<OneChatkfVisitor> visitors =
                oneChatkfVisitorService.findList(OneChatkfVisitor.builder()
                                .currentState(1)
                                .userAccount(SecurityUtils.getUsername())
                        .build());
        return R.ok(visitors.stream()
                .sorted(Comparator.comparing(
                        OneChatkfVisitor::getUpdateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList()));
    }


    /**
     * 获取一个最久接入排队中的访客
     * @return
     */
    @GetMapping("/findAccessVisitor")
    public R<OneChatkfVisitor> findAccessVisitor(){

        OneChatkfVisitor accessVisitor = oneChatkfVisitorService.findAccessVisitor();

        return R.ok(accessVisitor);
    }


    /**
     * 根据指定条件获取访客(一个)
     * @param oneChatkfVisitor
     * @return
     */
    @GetMapping("/findAppointVisitor")
    public  R<OneChatkfVisitor> findAppointVisitor(OneChatkfVisitor oneChatkfVisitor){
        OneChatkfVisitor appointVisitor = oneChatkfVisitorService
                .findAppointVisitor(oneChatkfVisitor.getKfRuleId().toString(), oneChatkfVisitor.getVisitorId(), oneChatkfVisitor.getUserAccount());



        return R.ok(appointVisitor);
    }


    /**
     * 根据指定条件获取访客(多个)
     * @param oneChatkfVisitor
     * @return
     */
    @GetMapping("/findAppointVisitorList")
    public R<List<OneChatkfVisitor>> findAppointVisitorList(OneChatkfVisitor oneChatkfVisitor){

        List<OneChatkfVisitor> chatkfVisitors = oneChatkfVisitorService.list(new LambdaQueryWrapper<OneChatkfVisitor>()
                .eq(StringUtils.isNotEmpty(oneChatkfVisitor.getVisitorId()),OneChatkfVisitor::getVisitorId, oneChatkfVisitor.getVisitorId())
                .eq(null != oneChatkfVisitor.getKfRuleId(),OneChatkfVisitor::getKfRuleId, oneChatkfVisitor.getKfRuleId())
                        .in(StringUtils.isNotEmpty(oneChatkfVisitor.getUserAccount()),OneChatkfVisitor::getUserAccount, Arrays.stream(oneChatkfVisitor.getUserAccount().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList())));

        return R.ok(chatkfVisitors);
    }




    /**
     * 新增或更新访客
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/addOrUpdate")
    public R<OneChatkfVisitor> addOrUpdate(@RequestBody OneChatkfVisitor oneChatkfVisitor){

        OneChatkfVisitor chatkfVisitor=oneChatkfVisitorService.addOrUpdate(oneChatkfVisitor);

        return R.ok(chatkfVisitor);
    }


    /**
     * 更新客服状态
     * @return
     */
    @PostMapping("/updateKfSate")
    public R updateKfSate(@RequestBody OneChatKfRuleScope oneChatKfRuleScope){

        iOneChatKfRuleScopeService.updateKfSate(oneChatKfRuleScope);

        return R.ok();
    }
    /**
     * 结束会话
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/endChat")
    public R endChat(@RequestBody OneChatkfVisitor oneChatkfVisitor){

        OneChatkfVisitor oldOneChatkfVisitor = oneChatkfVisitorService.getById(oneChatkfVisitor.getId());

        if(null != oldOneChatkfVisitor){
            oldOneChatkfVisitor.setCurrentState(OneChatVisitorSate.END_STATE);
            oldOneChatkfVisitor.setUpdateTime(new Date());
            oneChatkfVisitorService.updateById(oldOneChatkfVisitor);
            remoteImService.endChat(VisitorStateDto.builder()
                            .kfVisitorId(oneChatkfVisitor.getId().toString())
                            .kfRuleId(oldOneChatkfVisitor.getKfRuleId().toString())
                            .visitorId(oldOneChatkfVisitor.getVisitorId())
                            .userAccount(oldOneChatkfVisitor.getUserAccount())
                            .webStyleId(oldOneChatkfVisitor.getWebStyleId().toString())
                    .build(), SecurityConstants.INNER);
        }

        return R.ok();
    }


    /**
     * 接入会话
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/accessChat")
    public R accessChat(@RequestBody OneChatkfVisitor oneChatkfVisitor){

        OneChatkfVisitor oldOneChatkfVisitor = oneChatkfVisitorService.getById(oneChatkfVisitor.getId());

        if(null != oldOneChatkfVisitor){

            oldOneChatkfVisitor.setCurrentState(OneChatVisitorSate.RECEIVE_STATE);
            oldOneChatkfVisitor.setUpdateTime(new Date());
            oneChatkfVisitorService.updateById(oldOneChatkfVisitor);

            remoteImService.accessChat(VisitorStateDto.builder()
                    .kfVisitorId(oneChatkfVisitor.getId().toString())
                    .kfRuleId(oldOneChatkfVisitor.getKfRuleId().toString())
                    .visitorId(oldOneChatkfVisitor.getVisitorId())
                    .userAccount(oldOneChatkfVisitor.getUserAccount())
                    .webStyleId(oldOneChatkfVisitor.getWebStyleId().toString())
                    .build(), SecurityConstants.INNER);
        }

        return R.ok();
    }


    /**
     * 访客打标签
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/makeTag")
    public R<List<OneChatKfVisitorTagRel>> makeTag(@RequestBody OneChatkfVisitor oneChatkfVisitor){

        List<OneChatKfVisitorTagRel> tagRels = oneChatKfVisitorTagRelService.makeTag(oneChatkfVisitor);

        return R.ok(tagRels);
    }


    /**
     * 获取消息列表
     * @param kfVisitorId
     * @return
     */
    @GetMapping("/getMsgList/{kfVisitorId}")
    public R< List<OneChatKfVisitorMsg> > getMsgList(@PathVariable Long kfVisitorId){

        List<OneChatKfVisitorMsg> oneChatKfVisitorMsgs = oneChatKfVisitorMsgService.getMsgList(kfVisitorId);

        return R.ok(oneChatKfVisitorMsgs);
    }


    /**
     * 根据id获取访客详情
     * @param id
     * @return
     */
    @GetMapping("/getOneChatkfVisitorById/{id}")
    public R<OneChatkfVisitor> getOneChatkfVisitorById(@PathVariable Long id){
        OneChatkfVisitor oneChatkfVisitor = oneChatkfVisitorService.getOneChatkfVisitorById(id);
        return R.ok(oneChatkfVisitor);
    }


    /**
     * 访客消息入库
     * @param kfVisitorMsg
     * @return
     */
    @PostMapping("/addMsgVisitor")
    public R<OneChatKfVisitorMsg> addMsgVisitor(@RequestBody OneChatKfVisitorMsg kfVisitorMsg){
        oneChatKfVisitorMsgService.save(kfVisitorMsg);
        return R.ok(kfVisitorMsg);
    }


    /**
     * 设置消息已读
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/setReadMsg")
    public R setReadMsg(@RequestBody OneChatkfVisitor oneChatkfVisitor){

        oneChatKfVisitorMsgService.update(OneChatKfVisitorMsg.builder().readReceipt(OneChatReadMsgState.readReceipt).build(), new LambdaQueryWrapper<OneChatKfVisitorMsg>()
                .eq(OneChatKfVisitorMsg::getKfVisitorId,oneChatkfVisitor.getId()));


        return R.ok();

    }


    /**
     * 获取排队中的访客
     * @param kfRuleId
     * @return
     */
    @GetMapping("/getIdleStateCount/{kfRuleId}")
   public R<OneChatStateCountDto> getIdleStateCount(@PathVariable String kfRuleId){
        OneChatStateCountDto stateCountDto=new OneChatStateCountDto();

        stateCountDto.setIdleNumber(
                oneChatkfVisitorService.count(new LambdaQueryWrapper<OneChatkfVisitor>()
                        .eq(OneChatkfVisitor::getKfRuleId,kfRuleId)
                        .eq(OneChatkfVisitor::getCurrentState, OneChatVisitorSate.IDLE_STATE))
        );//排队中客户数

        return R.ok(stateCountDto);


   }
}
