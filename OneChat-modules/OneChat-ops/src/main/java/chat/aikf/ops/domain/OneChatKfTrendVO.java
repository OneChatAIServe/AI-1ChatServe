package chat.aikf.ops.domain;


import lombok.Data;

@Data
public class OneChatKfTrendVO {
    /**统计时间**/
    private String countDate;

    /** 当日咨询接待数 */
    private Long todayHandled;
}
