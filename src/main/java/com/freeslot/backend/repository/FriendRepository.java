package com.freeslot.backend.repository;

import com.freeslot.backend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserId(Long userId);
    List<Friend> findByUserIdAndStatus(Long userId, String status);
    List<Friend> findByFriendIdAndStatus(Long friendId, String status);
    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
