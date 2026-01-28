package chat.aikf.im.tio.utils;

import chat.aikf.im.tio.model.GuestIdentityMsgDto;
import chat.aikf.im.tio.model.UserIdentityMsgDto;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class PingUtils {

    // 辅助方法：判断是否为 ping 心跳
    public static boolean isPingMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            JSONObject json = JSONUtil.parseObj(text);
            return "ping".equals(json.getStr("type"));
        } catch (Exception e) {
            // 非 JSON 或解析失败，不是 ping
            return false;
        }
    }


    //构造ping的响应(访客端)
    public static String buildPongMessageToGuest(){


        return JSONUtil.toJsonStr(GuestIdentityMsgDto.builder().initState(3).build());
    }

    //构造ping的响应(管理端)
    public static String buildPongMessageToUser(){


        return JSONUtil.toJsonStr(UserIdentityMsgDto.builder().initState(3).build());
    }
}
