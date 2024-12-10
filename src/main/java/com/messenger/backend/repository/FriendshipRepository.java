package com.messenger.backend.repository;

import com.messenger.backend.friendship.FriendDto;
import com.messenger.backend.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    @Query("SELECT new com.messenger.backend.friendship.FriendDto(f.id, " +
            "CASE WHEN f.userId = :userId THEN u.username ELSE f2.username END, " +
            "f.status) " +
            "FROM Friendship f " +
            "LEFT JOIN User u ON f.friendId = u.id " +
            "LEFT JOIN User f2 ON f.userId = f2.id " +
            "WHERE (f.userId = :userId OR f.friendId = :userId) AND f.status = 'ACCEPTED'")
    List<FriendDto> findFriendsWithStatusByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.messenger.backend.friendship.FriendDto(f.id, u.username, f.status) " +
            "FROM Friendship f " +
            "JOIN User u ON f.friendId = u.id " +
            "WHERE f.userId = :userId AND f.status = 'PENDING'")
    List<FriendDto> findOutgoingRequestsByUserId(@Param("userId") Long userId);

    // Получение всех входящих заявок для текущего пользователя
    @Query("SELECT new com.messenger.backend.friendship.FriendDto(f.id, u.username, f.status) " +
            "FROM Friendship f JOIN User u ON f.userId = u.id " +
            "WHERE f.friendId = :friendId AND f.status = 'PENDING'")
    List<FriendDto> findPendingRequests(@Param("friendId") Long friendId);

    // Обновление статуса заявки
    @Modifying
    @Query("UPDATE Friendship f SET f.status = :status WHERE f.id = :friendshipId")
    int updateFriendshipStatus(@Param("friendshipId") Long friendshipId, @Param("status") String status);
}

