package chat.aikf.ops.service;


import chat.aikf.common.core.domain.R;
import chat.aikf.common.core.web.domain.BaseEntity;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.domain.OneChatKfCsStatsVO;
import chat.aikf.ops.domain.OneChatKfTrendVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_ kf_visitor(客服访客)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatkfVisitorService extends IService<OneChatkfVisitor> {


    /**
     * 访客管理列表
     * @param oneChatkfVisitor
     * @return
     */
    List<OneChatkfVisitor> findList(OneChatkfVisitor oneChatkfVisitor);


    /**
     * 根据id获取访客详情
     * @param id
     * @return
     */
    public OneChatkfVisitor getOneChatkfVisitorById(Long id);


    /**
     * 数据概览-汇总
     * @return
     */
    OneChatKfCsStatsVO countOneChatKfCsStats(BaseEntity baseEntity);


    /**
     * 数据概览-趋势
     * @param baseEntity
     * @return
     */
    List<OneChatKfTrendVO> countOneChatKfTrendVo(BaseEntity baseEntity);


    /**
     * 新增或更新访客
     * @param oneChatkfVisitor
     */
    OneChatkfVisitor addOrUpdate(OneChatkfVisitor oneChatkfVisitor);


    /**
     * 访客结束会话
     * @param oneChatkfVisitor
     * @return
     */
    OneChatkfVisitor endChat(OneChatkfVisitor oneChatkfVisitor);


    /**
     * 获取一个接入访客
     * @return
     */
    OneChatkfVisitor findAccessVisitor();


    /**
     * 根据条件获取指定的访客
     * @param kfRuleId
     * @param visitorId
     * @param userAccount
     * @return
     */
    OneChatkfVisitor findAppointVisitor(String kfRuleId, String visitorId,String userAccount);


}
