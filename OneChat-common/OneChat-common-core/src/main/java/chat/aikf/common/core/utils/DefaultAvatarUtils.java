package chat.aikf.common.core.utils;

import java.util.Random;


/**
 * 客服访客默认头像
 */
import java.util.HashSet;
import java.util.Set;

public class DefaultAvatarUtils {

    // 预定义所有头像文件名（相对路径）
    private static final String[] AVATAR_FILES = {
            "/file/avatars/avatar-1.png",
            "/file/avatars/avatar-2.png",
            "/file/avatars/avatar-3.png",
            "/file/avatars/avatar-4.png",
            "/file/avatars/avatar-5.png",
            "/file/avatars/avatar-6.png",
            "/file/avatars/avatar-7.png",
            "/file/avatars/avatar-8.png",
            "/file/avatars/avatar-9.png",
            "/file/avatars/avatar-10.png"
    };


    //默认头像
    private static final String DEFAULT_AVATRA="/avatars/avatar-default.png";

    // 为了快速查找，构建一个 Set（只存文件名部分，如 "avatar-1.png"）
    private static final Set<String> AVATAR_FILE_NAMES = new HashSet<>();

    static {
        for (String path : AVATAR_FILES) {
            // 提取最后一段：avatar-1.png
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            AVATAR_FILE_NAMES.add(fileName);
        }
    }

    private static final Random RANDOM = new Random();

    /**
     * 获取随机默认头像的相对路径
     */
    public static String getRandomAvatarPath() {
        return AVATAR_FILES[RANDOM.nextInt(AVATAR_FILES.length)];
    }

    /**
     * 判断给定的文件名（如 "avatar-1.png"）是否是合法的默认头像
     */
    public static boolean isValidAvatarFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        return AVATAR_FILE_NAMES.contains(fileName);
    }

    /**
     * 获取完整路径（如果文件名合法）
     * @return 合法则返回 "/file/avatars/avatar-x.png"，否则返回 null
     */
    public static String getAvatarPathByName(String fileName) {
        if (isValidAvatarFileName(fileName)) {
            return "/avatars/" + fileName;
        }
        return DEFAULT_AVATRA;
    }

//    /**
//     * 获取所有头像路径（用于调试或测试）
//     */
//    public static String[] getAllAvatarPaths() {
//        return AVATAR_FILES.clone();
//    }
}