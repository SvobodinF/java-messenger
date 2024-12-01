package com.messenger.backend.friendship;

public record FriendDto(Long id, String friendName, String status) {
    public FriendDto(Long id, String friendName, String status) {
        this.id = id;
        this.friendName = friendName;
        this.status = status;
    }
}
