package chat.aikf.ops.api.domain;

import chat.aikf.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @TableName one_chat_kf_rule
 */
@TableName(value ="one_chat_kf_rule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneChatKfRule extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;


    /**
     * 客服组名称
     */
    private String ruleName;

    /**
     * 在线时间(1-7分别代表周一到周日，多个使用逗号隔开字符串)
     */
    private String cyclePeriodDay;

    /**
     * 在线开始时间00:00格式
     */
    @JsonFormat(pattern = "HH:mm")
    private Date cyclePeriodStartTime;

    /**
     * 在线结束时间00:00格式
     */
    @JsonFormat(pattern = "HH:mm")
    private Date cyclePeriodEndTime;


    /**
     * 分配规则(1:轮流分配;2:空闲分配;3:随机分配)
     */
    private Integer allocateRule;


    /**
     * 高级规则(1:同访客优先分配上次接待成员)后续可拓展，多个使用逗号隔开
     */
    private String higherRule;


    /**
     * 接待语
     */
    private String receiveMsg;

    /**
     * 离线提示语
     */
    private String offlineTipMsg;


    /**
     * 结束提示语
     */
    private String endMsg;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;


    /**
     * 规则范围
     */
    @TableField(exist = false)
    private List<OneChatKfRuleScope> ruleScopeList;


    /**
     * 判断当前时间是否在客服规则的工作时间范围内
     *
     * @param rule 客服规则对象
     * @return true表示在工作时间范围内，false表示不在
     */
    public static boolean isRuleInWorkingHours(OneChatKfRule rule) {
        if (rule == null) {
            return false;
        }

        Date now = new Date();

        // 1. 先判断当天是周几，是否在工作日内
        String cyclePeriodDay = rule.getCyclePeriodDay();
        if (cyclePeriodDay != null && !cyclePeriodDay.trim().isEmpty()) {
            int currentDay = LocalDate.from(now.toInstant().atZone(java.time.ZoneId.systemDefault())).getDayOfWeek().getValue();
            String[] workDays = cyclePeriodDay.split(",");
            boolean isWorkDay = false;
            for (String workDay : workDays) {
                try {
                    if (Integer.parseInt(workDay.trim()) == currentDay) {
                        isWorkDay = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            if (!isWorkDay) {
                return false;
            }
        }

        // 2. 再判断时间是否在开始时间与结束时间范围内
        Date startTime = rule.getCyclePeriodStartTime();
        Date endTime = rule.getCyclePeriodEndTime();
        if (startTime != null && endTime != null) {
            LocalTime currentTime = LocalTime.from(now.toInstant().atZone(java.time.ZoneId.systemDefault()));
            LocalTime workStartTime = LocalTime.of(startTime.getHours(), startTime.getMinutes());
            LocalTime workEndTime = LocalTime.of(endTime.getHours(), endTime.getMinutes());

            if (workEndTime.isBefore(workStartTime)) {
                // 跨天情况
                return currentTime.isBefore(workEndTime) || !currentTime.isBefore(workStartTime);
            } else {
                // 正常情况
                return !currentTime.isBefore(workStartTime) && currentTime.isBefore(workEndTime);
            }
        }

        return true;
    }


//    public static void main(String[] args) {
//        OneChatKfRule rule=new OneChatKfRule();
//
//        // 设置工作时间 9:00 - 18:00
//        Date startTime = setTime(9, 0);  // 9:00
//        Date endTime = setTime(17, 40);   // 18:00
//        rule.setCyclePeriodStartTime(startTime);
//        rule.setCyclePeriodEndTime(endTime);
//
//
//        rule.setCyclePeriodDay("1,2,3,4,5");
//
//
//
//        System.out.println( isRuleInWorkingHours(rule));
//
//    }
//
//
//    /**
//     * 设置指定时分的时间
//     */
//    private static Date setTime(int hour, int minute) {
//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
//        cal.set(java.util.Calendar.MINUTE, minute);
//        cal.set(java.util.Calendar.SECOND, 0);
//        cal.set(java.util.Calendar.MILLISECOND, 0);
//        return cal.getTime();
//    }


}