package dev.kush.aicodeagent.chat;

import dev.kush.aicodeagent.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);
    private final ChatModel openAiChatModel;
    private final ChatModel azureChatModel;
    private final ChatModel azureMiniChatModel;

    public AgentService(@Qualifier("openAiChatModel") ChatModel openAiChatModel,
                        @Qualifier("azureChatModel") ChatModel azureChatModel,
                        @Qualifier("azureMiniChatModel") ChatModel azureMiniChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.azureChatModel = azureChatModel;
        this.azureMiniChatModel = azureMiniChatModel;
    }

    private ChatModel getChatModel(String beanName) {
        return switch (beanName) {
            case "openAiChatModel" -> openAiChatModel;
            case "azureChatModel" -> azureChatModel;
            case "azureMiniChatModel" -> azureMiniChatModel;
            default -> throw new IllegalArgumentException("Unknown chat model: " + beanName);
        };
    }

    public ChatModel desideChatModel(String query) {
        ChatClient chatClient = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        final ChatModelDecision chatModelDecision = chatClient.prompt()
                .system("You are a helpful AI assistant. your task is to help decide which chat model to use based on the query.")
                .user("""
                        Here is user query: %s
                        Based on the query, decide which chat model to use, return response as a JSON object with the following structure:
                        {
                            "beanName": "name of the chat model bean",
                            "reason": "reason for choosing this chat model"
                        }
                        Here are information about the chat models:
                        -------------------------------------------------
                        %s
                        -------------------------------------------------
                        """.formatted(query, ModelType.getAllInfoAsString()))
                .call()
                .entity(ChatModelDecision.class);

        if (chatModelDecision == null) {
            throw new IllegalStateException("Chat model decision is null. Please check the chat model decision logic.");
        }
        log.info("Chat model decision: beanName={}, reason={}", chatModelDecision.beanName(), chatModelDecision.reason());
        return getChatModel(chatModelDecision.beanName());
    }

    public record ChatModelDecision(String beanName, String reason) {

    }
}
