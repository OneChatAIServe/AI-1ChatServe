package chat.aikf.ai.factory;

import chat.aikf.ai.config.AiModelsProperties;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.*;


@Component
public class ModelFactory {

    private static final Logger log = LoggerFactory.getLogger(ModelFactory.class);

    private final AiModelsProperties properties;

    private final Map<String, ChatLanguageModel> chatModelCache = new ConcurrentHashMap<>();
    private final Map<String, StreamingChatLanguageModel> streamingModelCache = new ConcurrentHashMap<>();

    public ModelFactory(AiModelsProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void initializeModels() {
        for (String modelName : properties.getEnabled()) {
            modelName = modelName.trim();
            AiModelsProperties.ModelConfig config = properties.getConfigs().get(modelName);

            if (config == null || !config.isValid()) {
                log.warn("模型 [{}] 配置缺失或 apiKey 为空，跳过初始化", modelName);
                continue;
            }

            // 构建同步模型
            ChatLanguageModel chatModel = OpenAiChatModel.builder()
                    .apiKey(config.getApiKey())
                    .baseUrl(config.getBaseUrl())
                    .modelName(config.getModelName())
                    .timeout(Duration.ofSeconds(30))
                    .build();
            chatModelCache.put(modelName, chatModel);

            // 构建流式模型
            StreamingChatLanguageModel streamingModel = OpenAiStreamingChatModel.builder()
                    .apiKey(config.getApiKey())
                    .baseUrl(config.getBaseUrl())
                    .modelName(config.getModelName())
                    .timeout(Duration.ofSeconds(30))
                    .build();
            streamingModelCache.put(modelName, streamingModel);

            log.info("已加载模型: {} | 同步={}, 流式={}",
                    modelName,
                    chatModel.getClass().getSimpleName(),
                    streamingModel.getClass().getSimpleName());
        }

        if (chatModelCache.isEmpty()) {
            log.warn("未加载任何 AI 模型！请检查 ai.models 配置");
        }
    }

    public ChatLanguageModel getChatModel(String modelName) {
        ChatLanguageModel model = chatModelCache.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("同步模型未启用或配置缺失: " + modelName +
                    "，可用模型: " + chatModelCache.keySet());
        }
        return model;
    }

    public StreamingChatLanguageModel getStreamingModel(String modelName) {
        StreamingChatLanguageModel model = streamingModelCache.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("流式模型未启用或配置缺失: " + modelName +
                    "，可用模型: " + streamingModelCache.keySet());
        }
        return model;
    }

    public List<String> getEnabledModels() {
        return new ArrayList<>(chatModelCache.keySet());
    }
}