package chat.aikf.im.allocation.enums;

public enum AssignStrategyType {
    RANDOM(3), //随机
    ROUND_ROBIN(1),//轮训
    LEAST_BUSY(2); //空闲分配

    private final Integer code;

    AssignStrategyType(Integer code) {
        this.code = code;
    }

    public static AssignStrategyType fromCode(Integer code) {
        for (AssignStrategyType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown strategy: " + code);
    }
}
