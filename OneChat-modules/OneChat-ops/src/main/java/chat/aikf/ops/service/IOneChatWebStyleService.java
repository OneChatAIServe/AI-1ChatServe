package chat.aikf.ops.service;

import chat.aikf.ops.api.domain.OneChatWebStyle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author robin
* @description 针对表【one_chat_web_style(网页接入样式)】的数据库操作Service
* @createDate 2025-12-12 11:04:32
*/
public interface IOneChatWebStyleService extends IService<OneChatWebStyle> {

    /**
     * 查询网页接入列表
     * @param webStyle
     * @return
     */
    List<OneChatWebStyle> findList(OneChatWebStyle webStyle);
}
