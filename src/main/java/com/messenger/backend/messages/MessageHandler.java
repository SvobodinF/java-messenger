package com.messenger.backend.messages;

import com.messenger.backend.domain.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageHandler {

    private final MessageService messageService;

    public MessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam Long senderId,
                                              @RequestParam String friendName,
                                              @RequestParam String text) {
        String result = messageService.sendMessage(senderId, friendName, text);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dialogues")
    public ResponseEntity<List<String>> getDialogues(@RequestParam Long userId) {
        List<String> dialogues = messageService.getDialogues(userId);
        return ResponseEntity.ok(dialogues);
    }

    @GetMapping("/with-friend")
    public ResponseEntity<List<Message>> getMessagesWithFriend(@RequestParam Long userId,
                                                               @RequestParam String friendName) {
        List<Message> messages = messageService.getMessagesWithFriend(userId, friendName);
        return ResponseEntity.ok(messages);
    }
}
