package chat.aikf.ops.mapper;

import chat.aifk.common.datascope.annotation.DataScope;
import chat.aikf.common.core.web.domain.BaseEntity;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.domain.OneChatKfCsStatsVO;
import chat.aikf.ops.domain.OneChatKfTrendVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_ kf_visitor(客服访客)】的数据库操作Mapper
* @createDate 2025-12-12 11:04:32
* @Entity chat.aikf.ops.domain.OneChat kfVisitor
*/
public interface OneChatkfVisitorMapper extends BaseMapper<OneChatkfVisitor> {


    List<OneChatkfVisitor> findList(@Param("visitor") OneChatkfVisitor visitor);


    OneChatKfCsStatsVO countOneChatKfCsStats();


    @DataScope(deptAlias = "oc", userAlias = "oc")
    List<OneChatKfTrendVO> countOneChatKfTrendVo(@Param("baseEntity") BaseEntity baseEntity);

}




