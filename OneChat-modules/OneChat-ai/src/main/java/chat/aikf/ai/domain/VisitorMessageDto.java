package chat.aikf.ai.domain;

import lombok.Data;

@Data
public class VisitorMessageDto {
    /**
     * 访客原始消息
     */
    private String visitorMessage;

    /**
     * 可选：如 "qwen-max", "gpt-4o-mini"
     */
    private String preferredModel;


}