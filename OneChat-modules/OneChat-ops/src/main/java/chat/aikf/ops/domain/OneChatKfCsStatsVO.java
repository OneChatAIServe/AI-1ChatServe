package chat.aikf.ops.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfCsStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 累计接待数 */
    private Long totalHandled;

    /** 今日接待数 */
    private Long todayHandled;

    /** 在线客服数 */
    private Long onlineAgents;

    /** 排队中 */
    private Long queuing;

    /** 对话中 */
    private Long chatting;

    /** 已结束 */
    private Long ended;
}
