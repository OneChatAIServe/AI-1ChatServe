package chat.aikf.common.core.utils;


import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.IPv4;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 基于 ip2region 3.3.1 的 IP 市级地理位置解析工具（真实可用版）
 * - 兼容 Spring Boot 3 打包为 JAR
 * - 无需向量索引（3.x 已内置）
 * - 仅返回市级名称（如：南京市）
 */
@Slf4j
public class IpLocationUtils {


    // ✅ 必须是磁盘上的真实文件路径
    private static final String XDB_PATH = "/usr/local/app/OneChat/ip/ip2region_v4.xdb";

    private static volatile Searcher searcher = null;

    static {
        try {
            File xdbFile = new File(XDB_PATH);
            if (!xdbFile.exists()) {
                log.error("xdb 文件不存在: {}", xdbFile.getAbsolutePath());
                searcher = null;
            }else{
                // 关键：使用 newWithFileOnly —— 只持有一个 RandomAccessFile 句柄，不加载内容到堆！
                searcher = Searcher.newWithFileOnly(new IPv4(), xdbFile);
                log.info("ip2region 使用文件句柄模式加载成功: {}", xdbFile.getAbsolutePath());
            }



        } catch (IOException e) {
            log.error("初始化 ip2region 失败", e);
            searcher = null;
        }
    }



    /**
     * 根据 IP 获取市级地理位置（如：南京市、广州市）
     */
    public static String getCityByIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return "未知";
        }
        if (isInternalIp(ip)) {
            return "内网IP";
        }
        if (searcher == null) {
            return "未知";
        }
        try {
            String region = searcher.search(ip); // ✅ 直接 search，无需 vectorIndex
            return extractCity(region);
        } catch (Exception e) {
            log.warn("IP 解析失败: {}", ip, e);
            return "未知";
        }
    }

    private static String extractCity(String region) {
        if (region == null || region.isEmpty() || region.startsWith("0|")) {
            return "未知";
        }
        String[] parts = region.split("\\|");
        // 格式：国家|省份|城市|运营商 （如：中国|江苏省|南京市|电信）
        if (parts.length >= 3 && !"0".equals(parts[2])) {
            return parts[2].replaceAll("市$", ""); // 返回“南京”而非“南京市”
        }
        return "未知";
    }

    private static boolean isInternalIp(String ip) {
        try {
            java.net.InetAddress addr = java.net.InetAddress.getByName(ip);
            return addr.isAnyLocalAddress() ||
                    addr.isLoopbackAddress() ||
                    addr.isLinkLocalAddress() ||
                    addr.isSiteLocalAddress();
        } catch (Exception e) {
            return false;
        }
    }
}