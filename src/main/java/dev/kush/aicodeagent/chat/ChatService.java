package dev.kush.aicodeagent.chat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final AgentService agentService;
    private final VectorStore vectorStore;
    private final ToolService toolService;
    private final IndexingService indexingService;

    public ChatService(AgentService agentService, VectorStore vectorStore, ToolService toolService, IndexingService indexingService) {
        this.agentService = agentService;
        this.vectorStore = vectorStore;
        this.toolService = toolService;
        this.indexingService = indexingService;
    }


    public Query chat(Query query) {
        ChatModel chatModel = agentService.decideChatModel(query.text());
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
                .build();

        String sessionId;
        if (StringUtils.isNotBlank(query.sessionId())) {
            sessionId = query.sessionId();
        } else {
            sessionId = UUID.randomUUID().toString();
        }

        String answer = chatClient.prompt()
                .user(query.text())
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();
        return new Query(answer, sessionId);
    }

    public Query edit(Query query) {
        ChatModel chatModel = agentService.decideChatModel(query.text());
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
                .build();

        String sessionId;
        if (StringUtils.isNotBlank(query.sessionId())) {
            sessionId = query.sessionId();
        } else {
            sessionId = UUID.randomUUID().toString();
        }

        String filter = "projectId" + "=='" + sessionId + "'";
        final List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query.text())
                        .filterExpression(filter)
                        .topK(3)
                        .build()
        );
        String relativeCode = documents.stream()
                .map(Document::getText)
                .reduce((a, b) -> a + "\n------------------------------------" + b)
                .orElse("");

        String answer = chatClient.prompt()
                .system("""
                        You are an AI code Agent that helps users write and edit code.
                        Remember to always write code in new git branch names 'agent'.
                        """)
                .user("""
                        User Query: %s,
                        related code: %s,
                        """.formatted(query.text(), relativeCode))
//                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId))
//                .tools(toolService.decideTools(query.text()).toArray())
                .tools(toolService.getAllTools().toArray())
                .call()
                .content();
        return new Query(answer, sessionId);
    }

    public String index(String sessionId) {
        try {
            indexingService.indexProject(sessionId);
            return "Indexing completed successfully.";
        } catch (Exception e) {
            return "Indexing failed: " + e.getMessage();
        }
    }


    public String init() {
        return UUID.randomUUID().toString();
    }
}
