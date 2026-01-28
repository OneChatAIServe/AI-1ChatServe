package chat.aikf.common.security.handler;


import chat.aikf.common.security.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DbBaseMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前登录用户名（从 SecurityContext 或 ThreadLocal）
        String currentUsername = getCurrentUsername();

        this.strictInsertFill(metaObject, "createBy", String.class, currentUsername);
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUsername);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUsername = getCurrentUsername();
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUsername);
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * 获取当前登录用户名（适配若依的 Shiro / Spring Security）
     */
    private String getCurrentUsername() {
       return SecurityUtils.getUsername();
    }
}
