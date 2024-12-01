package com.messenger.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    public Message() {
        // Конструктор по умолчанию
    }

    public Message(Long senderId, Long receiverId, String text) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.sentAt = LocalDateTime.now();
    }

    // Getters и Setters

    public Long getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }
}
