package dev.kush.aicodeagent.web;

import dev.kush.aicodeagent.chat.ChatService;
import dev.kush.aicodeagent.chat.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/init")
    String init() {
        return chatService.init();
    }

    @GetMapping("/ask")
    Query chat(@RequestBody Query query) {
        return chatService.chat(query);
    }

    @GetMapping("/edit")
    Query edit(@RequestBody Query query) {
        return chatService.edit(query);
    }

    @GetMapping("/index")
    String indexProject(@RequestBody String projectId) {
        return chatService.index(projectId);
    }

}
