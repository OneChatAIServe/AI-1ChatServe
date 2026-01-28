package chat.aikf.common.core.utils;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NonceUtils {
    // ====== 配置区（可根据需要调整）======

    /** 自定义纪元：2025-01-01 00:00:00 UTC（毫秒时间戳） */
    private static final long EPOCH = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
            .toInstant().toEpochMilli();

    /** 实例ID位数（最大1023） */
    private static final long INSTANCE_ID_BITS = 10L;
    /** 序列号位数（最大4095） */
    private static final long SEQUENCE_BITS = 12L;

    /** 最大实例ID */
    private static final long MAX_INSTANCE_ID = (1L << INSTANCE_ID_BITS) - 1;
    /** 每毫秒最大序列号 */
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    /** 实例ID左移位数 */
    private static final long INSTANCE_ID_SHIFT = SEQUENCE_BITS;
    /** 时间戳左移位数 */
    private static final long TIMESTAMP_LEFT_SHIFT = INSTANCE_ID_BITS + SEQUENCE_BITS;

    // ====== 运行时状态 ======

    private final long instanceId;
    private volatile long lastTimestamp = -1L;
    private final AtomicLong sequence = new AtomicLong(0);

    // ====== 单例（线程安全）======

    private static class Holder {
        static final NonceUtils INSTANCE = new NonceUtils();
    }

    public static NonceUtils getInstance() {
        return Holder.INSTANCE;
    }

    private NonceUtils() {
        this.instanceId = generateInstanceId();
        if (instanceId > MAX_INSTANCE_ID || instanceId < 0) {
            throw new IllegalStateException("Instance ID out of range: " + instanceId);
        }
    }

    /**
     * 生成纯数字唯一ID（19位）
     */
    public synchronized String nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            // 时钟回拨！简单处理：抛异常（生产环境可优化）
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");
        }

        if (lastTimestamp == timestamp) {
            // 同一毫秒，序列号自增
            sequence.compareAndSet(MAX_SEQUENCE, 0); // 达到上限则归零（可能重复！）
            sequence.incrementAndGet();
        } else {
            // 新毫秒，重置序列号
            sequence.set(0L);
        }

        lastTimestamp = timestamp;

        // 组装ID
        long id = ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (instanceId << INSTANCE_ID_SHIFT)
                | sequence.get();

        return Long.toString(id);
    }

    /**
     * 生成实例ID（基于IP和JVM PID模拟）
     */
    private long generateInstanceId() {
        try {
            // 方式1：从系统属性读取（推荐部署时指定）
            String configId = System.getProperty("numeric.id.instance.id");
            if (configId != null) {
                long id = Long.parseLong(configId);
                if (id >= 0 && id <= MAX_INSTANCE_ID) {
                    return id;
                }
            }

            // 方式2：基于IP哈希（简单去重）
            InetAddress ip = InetAddress.getLocalHost();
            byte[] addr = ip.getAddress();
            int hash = 0;
            for (byte b : addr) {
                hash = (hash * 31 + (b & 0xFF)) & 0x3FF; // 保留低10位
            }
            return hash;

        } catch (Exception e) {
            // 保底：使用随机数（重启可能冲突，但概率低）
            return (System.nanoTime() ^ Thread.currentThread().getId()) & 0x3FF;
        }
    }





}
