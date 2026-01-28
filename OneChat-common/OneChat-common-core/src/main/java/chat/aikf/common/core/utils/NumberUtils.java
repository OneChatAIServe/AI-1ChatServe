package chat.aikf.common.core.utils;

public class NumberUtils {
    public static Integer toInteger(Long value) {
        if (value == null) {
            return null; // 或抛异常，根据业务需求
        }
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("Value " + value + " cannot fit in an int");
        }
        return value.intValue(); // 等价于 (int) value
    }
}
