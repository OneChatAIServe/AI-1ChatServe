package chat.aikf.im.tio.utils;

import java.util.HashMap;
import java.util.Map;

public class UrlUtils {
    public static Map<String, String> parseQueryString(String qs) {
        Map<String, String> map = new HashMap<>();
        if (qs == null || qs.isEmpty()) return map;

        String[] pairs = qs.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                try {
                    key = java.net.URLDecoder.decode(key, "UTF-8");
                    value = java.net.URLDecoder.decode(value, "UTF-8");
                } catch (Exception ignored) {}
                map.put(key, value);
            }
        }
        return map;
    }
}
