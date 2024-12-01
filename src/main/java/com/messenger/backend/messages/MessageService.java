package com.messenger.backend.messages;

import com.messenger.backend.domain.Message;
import com.messenger.backend.repository.MessageRepository;
import com.messenger.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // Отправка сообщения
    public String sendMessage(Long senderId, String friendName, String text) {
        var friend = userRepository.findByUsername(friendName);
        if (friend == null) {
            return "Friend not found.";
        }

        Message message = new Message(senderId, friend.getId(), text);
        messageRepository.save(message);
        return "Message sent successfully.";
    }

    // Получение списка диалогов
    public List<String> getDialogues(Long userId) {
        List<Long> friendIds = messageRepository.findDialogues(userId);
        return userRepository.findAllById(friendIds)
                .stream()
                .map(user -> user.getUsername())
                .collect(Collectors.toList());
    }

    // Получение всех сообщений с конкретным другом
    public List<Message> getMessagesWithFriend(Long userId, String friendName) {
        var friend = userRepository.findByUsername(friendName);
        if (friend == null) {
            throw new IllegalArgumentException("Friend not found.");
        }

        return messageRepository.findMessagesWithFriend(userId, friend.getId());
    }
}
