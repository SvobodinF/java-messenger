package com.messenger.backend.repository;

import com.messenger.backend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Получение всех диалогов пользователя (все уникальные пары отправитель/получатель)
    @Query("SELECT DISTINCT m.receiverId FROM Message m WHERE m.senderId = :userId " +
            "UNION " +
            "SELECT DISTINCT m.senderId FROM Message m WHERE m.receiverId = :userId")
    List<Long> findDialogues(@Param("userId") Long userId);

    // Получение всех сообщений с конкретным другом
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId AND m.receiverId = :friendId) OR " +
            "(m.senderId = :friendId AND m.receiverId = :userId) " +
            "ORDER BY m.sentAt")
    List<Message> findMessagesWithFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
