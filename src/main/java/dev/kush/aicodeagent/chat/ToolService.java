package dev.kush.aicodeagent.chat;

import dev.kush.aicodeagent.tools.Tools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToolService {

    private final ChatModel chatModel;
    private final ApplicationContext applicationContext;

    public ToolService(@Qualifier("openAiChatModel") ChatModel chatModel, ApplicationContext applicationContext) {
        this.chatModel = chatModel;
        this.applicationContext = applicationContext;
    }

    public List<Object> decideTools(String query) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        String toolsString = chatClient.prompt()
                .system("""
                        You are a helpful assistant that decides which tools to use based on the user's query.
                        return comma separated string of tool names that you think are relevant to the query.
                        If someone asks you to write code, you should not write code directly.
                        first you should analyze the current codebase and decide where to write the code.
                        If someone asks you to write code, you should write it in different git branch.
                        """)
                .user("""
                        Decide which tools to use for the following query: %s
                        Tools available: %s
                        return comma separated string of bean names of tools that you think are relevant to the query.
                        only return the bean names of the tools, nothing else.
                        """.formatted(query, Tools.getAllInfoAsString()))
                .call()
                .content();
        List<String> toolNames = List.of(toolsString.split(","));
        return toolNames.stream()
                .map(String::trim)
                .map(applicationContext::getBean)
                .toList();
    }

    public List<Object> getAllTools() {
        List<Object> tools = new ArrayList<>();
        for (Tools tool: Tools.values()) {
            Object bean = applicationContext.getBean(tool.getBeanName());
            tools.add(bean);
        }
        return tools;
    }
}
