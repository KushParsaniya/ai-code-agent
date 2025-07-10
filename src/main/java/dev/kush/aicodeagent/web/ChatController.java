package dev.kush.aicodeagent.web;

import dev.kush.aicodeagent.chat.ChatService;
import dev.kush.aicodeagent.chat.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ask")
    private Query chat(@RequestBody Query query) {
        return chatService.chat(query);
    }
}
