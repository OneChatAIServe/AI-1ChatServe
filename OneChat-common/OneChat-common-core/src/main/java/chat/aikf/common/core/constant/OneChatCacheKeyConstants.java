package chat.aikf.common.core.constant;

public class OneChatCacheKeyConstants {

    // ==================== 业务模块分组 ====================
    public static class Module {
        public static final String IM = "im";           // 系统im模块所需缓存
    }




    // ==================== IM模块缓存Key规范 ====================
    public static class ImCacheKey {


        /**
         * 访客初始化会话标识
         */
        public static final String INIT_VISITOR_SESSION=  Module.IM+":init:visitor:%s:%s";

        /**
         * 客服组规则缓存
         */
        public static final String KF_RULE = Module.IM + ":kf:rule:%s";


        /**
         * 网页客服样式缓存key
         */
        public static final String KF_STYLE_WEB = Module.IM + ":kf:style:web:%s";


        /**
         * 样式-规则——员工-访客关系维护
         */
        public static final String KF_SERVING=Module.IM+":serving:%s:%s:%s";


        /**
         * 指定规则下当前分配到了哪个客服(主要面向:轮流分配)
         */
        public static final String CURRENT_RULE_ALLOCATE_KF=Module.IM+":current:style:rule:%s:%s";

    }

    // ==================== IM模块缓存Key生成方法 ====================
    public static class ImKeyGenerator {

        /**
         * 获取客服规则缓存Key
         */
        public static String getKfRuleKey(Long kfRuleId) {
            return String.format(ImCacheKey.KF_RULE, kfRuleId);
        }


        /**
         * 获取访客会话初始化标识
         * @param visitorId
         * @param kfRuleId
         * @return
         */
        public static String getInitVisitorSessionKey(String visitorId, String kfRuleId){

            return String.format(ImCacheKey.INIT_VISITOR_SESSION, visitorId,kfRuleId);
        }


        /**
         *  样式-规则——员工-访客关系标识
         * @param webStyleId
         * @param kfRuleId
         * @param userAccount
         * @return
         */

        public static  String getKfServingKey(String webStyleId,String kfRuleId, String userAccount){

            return String.format(ImCacheKey.KF_SERVING,webStyleId, kfRuleId,userAccount);
        }


        /**
         * 指定规则下当前分配到了哪个客服(主要面向:轮流分配)
         * @param webStyleId
         * @param kfRuleId
         * @return
         */
        public static String getCurrentRuleAllocateKfKey(String webStyleId,String kfRuleId){
            return String.format(ImCacheKey.CURRENT_RULE_ALLOCATE_KF,webStyleId,kfRuleId);
        }


        /**
         * 网页客服样式缓存key
         * @param kfWebStyleId
         * @return
         */
        public static String getKfStyleWebkey(Long kfWebStyleId){
            return String.format(ImCacheKey.KF_STYLE_WEB,kfWebStyleId);
        }

    }


    // ==================== 缓存TTL常量（秒为单位） ====================
    public static class CacheTTL {
        // 客服组规则缓存 - 15分钟
        public static final long KF_RULE = 900L;

        //规则——员工-访客关系 缓存时间 15分钟
        public static final long RELATION_TTL_MINUTES = 900L;

        //网页客服样式缓存时间，默认15分钟
        public static final long KF_STYLE_WEB_TTL = 900L;

       //缓存穿透规则过期时间 300秒
        public static final long CACHE_PENETRATE_TTL=300L;


    }





}
