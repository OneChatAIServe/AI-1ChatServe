package chat.aikf.ops.api;


import chat.aikf.common.core.constant.SecurityConstants;
import chat.aikf.common.core.constant.ServiceNameConstants;
import chat.aikf.common.core.domain.R;
import chat.aikf.ops.api.domain.OneChatKfVisitorMsg;
import chat.aikf.ops.api.domain.OneChatkfVisitor;
import chat.aikf.ops.api.domain.dto.OneChatStateCountDto;
import chat.aikf.ops.api.factory.RemoteKfVisitorFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "remoteKfVisitorService", value = ServiceNameConstants.OneChatOps, fallbackFactory = RemoteKfVisitorFallbackFactory.class)
public interface RemoteKfVisitorService {

    /**
     * 新增或更新访客
     * @param oneChatkfVisitor
     * @return
     */
    @PostMapping("/kfVisitor/addOrUpdate")
    public R<OneChatkfVisitor> addOrUpdate(@RequestBody OneChatkfVisitor oneChatkfVisitor,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 访客消息入库
     * @param kfVisitorMsg
     * @param source
     * @return
     */
    @PostMapping("/kfVisitor/addMsgVisitor")
    public R<OneChatKfVisitorMsg> addMsgVisitor(@RequestBody OneChatKfVisitorMsg kfVisitorMsg,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 获取当前访客排队中的数量
     * @param kfRuleId
     * @param source
     * @return
     */
    @GetMapping("/kfVisitor/getIdleStateCount/{kfRuleId}")
    public R<OneChatStateCountDto> getIdleStateCount(@PathVariable String kfRuleId,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 获取一个最久接入排队中的访客
     * @param source
     * @return
     */
    @GetMapping("/kfVisitor/findAccessVisitor")
    public R<OneChatkfVisitor> findAccessVisitor(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 根据指定条件获取访客(一个)
     * @param oneChatkfVisitor
     * @param source
     * @return
     */
    @GetMapping("/kfVisitor/findAppointVisitor")
    public  R<OneChatkfVisitor> findAppointVisitor(OneChatkfVisitor oneChatkfVisitor,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 根据指定条件获取访客(多个)
     * @param oneChatkfVisitor
     * @return
     */
    @GetMapping("/kfVisitor/findAppointVisitorList")
    public R<List<OneChatkfVisitor>> findAppointVisitorList(OneChatkfVisitor oneChatkfVisitor,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);




}
