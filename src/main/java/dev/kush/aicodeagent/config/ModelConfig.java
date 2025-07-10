package dev.kush.aicodeagent.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelConfig {

    private final AzureChatProperties azureChatProperties;
    private final AzureEmbeddingProperties azureEmbeddingProperties;
    private final OpenAiProperties openAiProperties;

    public ModelConfig(AzureChatProperties azureChatProperties, AzureEmbeddingProperties azureEmbeddingProperties, OpenAiProperties openAiProperties) {
        this.azureChatProperties = azureChatProperties;
        this.azureEmbeddingProperties = azureEmbeddingProperties;
        this.openAiProperties = openAiProperties;
    }

    @Bean("openAiChatModel")
    ChatModel openAiChatModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(openAiProperties.getApiKey())
                .baseUrl(openAiProperties.getBaseUrl())
                .build();
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(openAiProperties.getModel())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiChatOptions)
                .build();
    }

    @Bean(name = "azureChatModel")
    ChatModel azureChatModel() {
        var openAIClientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(azureChatProperties.getApiKey()))
                .endpoint(azureChatProperties.getBaseUrl());

        var openAIChatOptions = AzureOpenAiChatOptions.builder()
                .deploymentName(azureChatProperties.getModel())
                .build();

        return AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(openAIChatOptions)
                .build();
    }

    @Bean(name = "azureMiniChatModel")
    @Primary
    ChatModel azureMiniChatModel() {
        var openAIClientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(azureChatProperties.getApiKey()))
                .endpoint(azureChatProperties.getBaseUrl());

        var openAIChatOptions = AzureOpenAiChatOptions.builder()
                .deploymentName(azureChatProperties.getMiniModel())
                .build();

        return AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(openAIChatOptions)
                .build();
    }

    @Bean(name = "azureEmbeddingModel")
    EmbeddingModel azureEmbeddingModel() {
        var openAIClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(azureEmbeddingProperties.getApiKey()))
                .endpoint(azureEmbeddingProperties.getBaseUrl())
                .buildClient();

        var openAIChatOptions = AzureOpenAiEmbeddingOptions.builder()
                .deploymentName(azureEmbeddingProperties.getModel())
                .dimensions(768)
                .build();

        return new AzureOpenAiEmbeddingModel(openAIClient, MetadataMode.EMBED, openAIChatOptions);
    }
}
