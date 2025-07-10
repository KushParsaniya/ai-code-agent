package dev.kush.aicodeagent.chat;

import dev.kush.aicodeagent.tools.FileReaderTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {

    private final AgentService agentService;
    private final VectorStore vectorStore;
    private final FileReaderTools fileReaderTools;

    public ChatService(AgentService agentService, VectorStore vectorStore, FileReaderTools fileReaderTools) {
        this.agentService = agentService;
        this.vectorStore = vectorStore;
        this.fileReaderTools = fileReaderTools;
    }


    public Query chat(Query query) {
        ChatModel chatModel = agentService.desideChatModel(query.text());
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
                .tools(fileReaderTools)
                .call()
                .content();
        return new Query(answer, sessionId);
    }



}
