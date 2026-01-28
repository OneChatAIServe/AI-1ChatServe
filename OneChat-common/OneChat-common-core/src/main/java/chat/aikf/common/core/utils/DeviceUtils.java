package chat.aikf.common.core.utils;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class DeviceUtils {

    private static final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats() // 静默加载
            .withCache(1000)         // 启用 LRU 缓存
            .build();

    /**
     * 仅解析设备信息（不含语言）
     */
    public static DeviceInfo parseDevice(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return new DeviceInfo("Unknown", "Unknown", "Unknown", "未知语言");
        }
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return new DeviceInfo(
                agent.getValue("DeviceClass"),
                agent.getValue("OperatingSystemNameVersion"),
                agent.getValue("AgentNameVersion"),
                "未知语言" // 语言需单独传入 Accept-Language 才能准确获取
        );
    }

    /**
     * 完整解析：设备 + 语言（推荐使用此方法）
     */
    public static DeviceInfo parseDeviceWithLanguage(String userAgent, String acceptLanguage) {
        String deviceType = "Unknown";
        String os = "Unknown";
        String browser = "Unknown";

        if (userAgent != null && !userAgent.trim().isEmpty()) {
            UserAgent agent = userAgentAnalyzer.parse(userAgent);
            deviceType = agent.getValue("DeviceClass");
            os = agent.getValue("OperatingSystemNameVersion");
            browser = agent.getValue("AgentNameVersion");
        }

        String displayLanguage = parseAcceptLanguageToDisplay(acceptLanguage);
        return new DeviceInfo(deviceType, os, browser, displayLanguage);
    }

    /**
     * 将 Accept-Language 转换为中文展示名（如 "简体中文"、"英文"）
     */
    public static String parseAcceptLanguageToDisplay(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            return "未知语言";
        }

        // 取第一个语言（优先级最高）
        String primaryLang = acceptLanguage.split(",")[0].split(";")[0].trim().toLowerCase();

        // 标准化：zh-cn, zh-tw, en-us → 统一转为小写并处理变体
        if (primaryLang.startsWith("zh")) {
            if (primaryLang.contains("tw") || primaryLang.contains("hk")) {
                return "繁体中文";
            } else {
                return "简体中文"; // 默认简体（包括 zh, zh-cn, zh-sg 等）
            }
        } else if (primaryLang.startsWith("en")) {
            return "英文";
        } else if (primaryLang.startsWith("ja")) {
            return "日文";
        } else if (primaryLang.startsWith("ko")) {
            return "韩文";
        } else if (primaryLang.startsWith("fr")) {
            return "法文";
        } else if (primaryLang.startsWith("de")) {
            return "德文";
        } else if (primaryLang.startsWith("es")) {
            return "西班牙文";
        } else if (primaryLang.startsWith("ru")) {
            return "俄文";
        } else if (primaryLang.startsWith("pt")) {
            return "葡萄牙文";
        } else if (primaryLang.startsWith("it")) {
            return "意大利文";
        } else if (primaryLang.startsWith("ar")) {
            return "阿拉伯文";
        } else if (primaryLang.startsWith("th")) {
            return "泰文";
        } else if (primaryLang.startsWith("vi")) {
            return "越南文";
        } else if (primaryLang.startsWith("id") || primaryLang.startsWith("in")) {
            return "印尼文";
        } else if (primaryLang.startsWith("ms")) {
            return "马来文";
        }

        return "其他语言";
    }

    public static class DeviceInfo {
        // 访问设备
        private final String deviceType;
        // 访客操作系统
        private final String os;
        // 访客浏览器
        private final String browser;
        // 浏览器语言（展示用）
        private final String language;

        public DeviceInfo(String deviceType, String os, String browser, String language) {
            this.deviceType = deviceType;
            this.os = os;
            this.browser = browser;
            this.language = language;
        }

        // Getters
        public String getDeviceType() {
            return deviceType;
        }

        public String getOs() {
            return os;
        }

        public String getBrowser() {
            return browser;
        }

        public String getLanguage() {
            return language;
        }
    }
}