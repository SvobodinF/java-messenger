package com.messenger.backend.friendship;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipHandler {

    private final FriendshipService friendshipService;

    public FriendshipHandler(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/add-by-name")
    public ResponseEntity<String> addFriendByName(@RequestParam Long userId, @RequestParam String friendName) {
        String result = friendshipService.addFriendByName(userId, friendName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendDto>> getFriendsList(@RequestParam Long userId) {
        List<FriendDto> friends = friendshipService.getFriendsList(userId);
        friends.addAll(getOutgoingRequests(userId));

        return ResponseEntity.ok(friends);
    }

    // Получение списка входящих заявок
    @GetMapping("/requests")
    public ResponseEntity<List<FriendDto>> getPendingRequests(@RequestParam Long userId) {
        List<FriendDto> pendingRequests = friendshipService.getPendingRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    // Получение списка исходящих заявок
    public List<FriendDto> getOutgoingRequests(Long userId) {
        return friendshipService.getOutgoingRequests(userId);
    }

    // Изменение статуса заявки
    @PutMapping("/update-status")
    public ResponseEntity<String> updateFriendshipStatus(@RequestParam Long friendshipId, @RequestParam String status) {
        String result = friendshipService.updateFriendshipStatus(friendshipId, status);
        return ResponseEntity.ok(result);
    }
}