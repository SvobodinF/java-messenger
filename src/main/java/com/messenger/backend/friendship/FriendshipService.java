package com.messenger.backend.friendship;

import com.messenger.backend.domain.Friendship;
import com.messenger.backend.domain.User;
import com.messenger.backend.repository.FriendshipRepository;
import com.messenger.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public String addFriendByName(Long userId, String friendName) {
        // Найти друга по имени
        User friend = userRepository.findByUsername(friendName);
        if (friend == null) {
            return "User with the given name does not exist.";
        }

        // Проверить, что запрос не отправляется самому себе
        if (friend.getId().equals(userId)) {
            return "You cannot add yourself as a friend.";
        }

        // Проверить, что дружба еще не существует
        if (friendshipRepository.existsByUserIdAndFriendId(userId, friend.getId())) {
            return "Friendship request already exists.";
        }

        // Добавить запись в таблицу дружбы
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friend.getId());
        friendship.setStatus("PENDING");
        friendshipRepository.save(friendship);

        return "Friend request sent successfully.";
    }

    public List<FriendDto> getFriendsList(Long userId) {
        return friendshipRepository.findFriendsWithStatusByUserId(userId);
    }

    // Получение списка входящих заявок
    public List<FriendDto> getPendingRequests(Long userId) {
        return friendshipRepository.findPendingRequests(userId);
    }

    // Обновление статуса заявки
    @Transactional
    public String updateFriendshipStatus(Long friendshipId, String status) {
        int rowsUpdated = friendshipRepository.updateFriendshipStatus(friendshipId, status);
        if (rowsUpdated > 0) {
            return "Friendship status updated successfully.";
        }
        return "Failed to update friendship status.";
    }
}