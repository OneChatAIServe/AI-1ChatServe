package chat.aikf.ai.service.impl;

import chat.aikf.ai.factory.ModelFactory;
import chat.aikf.ai.prompt.PromptManager;
import chat.aikf.ai.service.OneChatAstService;
import chat.aikf.ai.utils.PromptUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class OneChatAstServiceImpl implements OneChatAstService {
    @Autowired
    private ModelFactory modelFactory;

    @Autowired
    private PromptManager promptManager;


    @Override
    public String recommendReply(String visitorMessage) {
        // 1. 构造消息
        PromptManager.PromptConfig prompt = promptManager.get("recommend-reply.recommend_reply");
        String userMsg = prompt.getUser().replace("{visitorMessage}", PromptUtils.escape(visitorMessage));

        List<ChatMessage> messages = Arrays.asList(
                SystemMessage.from(prompt.getSystem()),
                UserMessage.from(userMsg)
        );

        // 2. 获取可用模型列表（按配置优先级）
        List<String> models = modelFactory.getEnabledModels();
        if (models.isEmpty()) {
            throw new RuntimeException("无可用 AI 模型，请检查 ai.models 配置");
        }

        Exception lastError = null;
        AtomicInteger attemptCount = new AtomicInteger(0);

        for (String modelName : models) {
            attemptCount.incrementAndGet();
            try {
                // 直接获取同步模型
                ChatLanguageModel model = modelFactory.getChatModel(modelName);

                // 同步调用，直接返回完整响应
                ChatResponse chatResponse = model.chat(messages);


                String reply = Optional.ofNullable(chatResponse.aiMessage())
                        .map(AiMessage::text)
                        .filter(s -> !s.trim().isEmpty())
                        .map(String::trim)
                        .orElseThrow(() -> new RuntimeException("模型返回内容为空或无效"));

                log.info("使用模型 [{}] 成功生成回复（第 {} 次尝试）", modelName, attemptCount.get());

                return reply;
            } catch (Exception e) {
                lastError = e;
                log.warn("⚠️ 模型 [{}] 调用失败（第 {} 次尝试）: {}",
                        modelName, attemptCount.get(), e.getMessage());
            }
        }

        log.error("所有模型均调用失败，共尝试 {} 个", attemptCount.get(), lastError);
        throw new RuntimeException("所有 AI 模型均不可用", lastError);
    }



}
