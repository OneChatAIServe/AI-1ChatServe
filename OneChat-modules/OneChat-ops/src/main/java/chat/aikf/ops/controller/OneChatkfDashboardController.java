package chat.aikf.ops.controller;

import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.utils.StringUtils;
import chat.aikf.common.core.web.controller.BaseController;
import chat.aikf.common.core.web.domain.BaseEntity;
import chat.aikf.ops.domain.OneChatKfCsStatsVO;
import chat.aikf.ops.domain.OneChatKfTrendVO;
import chat.aikf.ops.service.IOneChatkfVisitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 数据概览
 */
@RestController
@RequestMapping("/dashboard")
public class OneChatkfDashboardController extends BaseController {

    @Autowired
    private IOneChatkfVisitorService oneChatkfVisitorService;


    /**
     * 数据概览-汇总
     * @return
     */
    @GetMapping("/countOneChatKfCsStats")
    public R<OneChatKfCsStatsVO> countOneChatKfCsStats(){
        OneChatKfCsStatsVO oneChatKfCsStatsVO = oneChatkfVisitorService.countOneChatKfCsStats(new BaseEntity());
        return R.ok(oneChatKfCsStatsVO);
    }



    /**
     * 数据概览-趋势
     * @return
     */
    @GetMapping("/countOneChatKfTrendVo")
    public R<List<OneChatKfTrendVO>> countOneChatKfTrendVo(BaseEntity baseEntity){
        if(StringUtils.isEmpty(baseEntity.getBeginTime())){
            return R.fail("开始时间不可为空");
        }

        if(StringUtils.isEmpty(baseEntity.getEndTime())){
            return R.fail("结束时间不可为空");
        }
        List<OneChatKfTrendVO> oneChatKfTrendVOS
                = oneChatkfVisitorService.countOneChatKfTrendVo(baseEntity);

        return R.ok(oneChatKfTrendVOS);
    }



}
