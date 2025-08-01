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
    private final ChatModel fastChatModel;

    public AgentService(@Qualifier("openAiChatModel") ChatModel openAiChatModel,
                        @Qualifier("azureChatModel") ChatModel azureChatModel,
                        @Qualifier("azureMiniChatModel") ChatModel azureMiniChatModel,
                        @Qualifier("fastAiChatModel") ChatModel fastChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.azureChatModel = azureChatModel;
        this.azureMiniChatModel = azureMiniChatModel;
        this.fastChatModel = fastChatModel;
    }

    private ChatModel getChatModel(String beanName) {
        return switch (beanName) {
            case "openAiChatModel" -> openAiChatModel;
            case "azureChatModel" -> azureChatModel;
            case "azureMiniChatModel" -> azureMiniChatModel;
            default -> throw new IllegalArgumentException("Unknown chat model: " + beanName);
        };
    }

    public ChatModel decideChatModel(String query) {
        ChatClient chatClient = ChatClient.builder(fastChatModel)
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

    // make it Async
    public String summarize(String query) {
        ChatClient chatClient = ChatClient.builder(fastChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        String prompt = "Summarize this message in 4-6 words as a session title: " + query;

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }

    public record ChatModelDecision(String beanName, String reason) {
    }
}
