package chat.aikf.common.core.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



/**
 * 日期工具类：生成连续日期区间（自动截断到当前日期）
 */
public class DateRangeUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 生成从 startDate 到 endDate（含）的连续日期字符串列表，
     * 但不会超过当前日期（today）。
     *
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate   结束日期（格式：yyyy-MM-dd）
     * @return 连续日期列表，最多到今天为止
     * @throws IllegalArgumentException 如果日期格式错误或 startDate > endDate（逻辑上）
     */
    public static List<String> getContinuousDatesUpToToday(String startDate, String endDate) {
        return getContinuousDatesUpToToday(startDate, endDate, "yyyy-MM-dd");
    }

    /**
     * 支持自定义输出格式
     */
    public static List<String> getContinuousDatesUpToToday(String startDate, String endDate, String outputFormat) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为 null");
        }

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate today = LocalDate.now(); // 获取当前系统日期

        // 如果用户输入的 endDate 比今天还晚，则截断为今天
        if (end.isAfter(today)) {
            end = today;
        }

        // 如果 start 已经在今天之后，则返回空列表（或只包含 start？根据业务）
        if (start.isAfter(today)) {
            return new ArrayList<>(); // 或抛异常，看需求
        }

        if (start.isAfter(end)) {
            // 正常不会发生，但防御性编程
            return new ArrayList<>();
        }

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(outputFormatter));
            current = current.plusDays(1);
        }

        return dates;
    }

    // ================== 可选：支持 LocalDate 对象输入 ==================

    public static List<String> getContinuousDatesUpToToday(LocalDate start, LocalDate end) {
        return getContinuousDatesUpToToday(start, end, "yyyy-MM-dd");
    }

    public static List<String> getContinuousDatesUpToToday(LocalDate start, LocalDate end, String outputFormat) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为 null");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }
        if (start.isAfter(today)) {
            return new ArrayList<>();
        }
        if (start.isAfter(end)) {
            return new ArrayList<>();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat);
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(formatter));
            current = current.plusDays(1);
        }
        return dates;
    }
}
