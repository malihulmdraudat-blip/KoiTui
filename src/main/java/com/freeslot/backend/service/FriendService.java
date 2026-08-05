package com.freeslot.backend.service;

import com.freeslot.backend.dto.FriendRequest;
import com.freeslot.backend.dto.FriendResponse;
import com.freeslot.backend.exception.BadRequestException;
import com.freeslot.backend.exception.ResourceNotFoundException;
import com.freeslot.backend.entity.Friend;
import com.freeslot.backend.entity.User;
import com.freeslot.backend.repository.FriendRepository;
import com.freeslot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public FriendResponse addFriend(String email, FriendRequest request) {
        User user = userService.getUserByEmail(email);

        User friend = userRepository.findByStudentId(request.getFriendStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("No student found with that student ID"));

        if (friend.getId().equals(user.getId())) {
            throw new BadRequestException("You cannot add yourself as a friend");
        }

        friendRepository.findByUserIdAndFriendId(user.getId(), friend.getId())
                .ifPresent(f -> {
                    throw new BadRequestException("This user is already in your friend list");
                });

        Friend friendEntry = new Friend();
        friendEntry.setUserId(user.getId());
        friendEntry.setFriendId(friend.getId());
        friendEntry.setStatus("ACCEPTED");
        friendRepository.save(friendEntry);

        return new FriendResponse(friend.getId(), friend.getStudentId(), friend.getName(), friend.getSemester());
    }

    @Transactional
    public void removeFriend(String email, Long friendId) {
        User user = userService.getUserByEmail(email);
        friendRepository.deleteByUserIdAndFriendId(user.getId(), friendId);
    }

    public List<FriendResponse> getMyFriends(String email) {
        User user = userService.getUserByEmail(email);

        return friendRepository.findByUserId(user.getId()).stream()
                .map(f -> {
                    User friend = userRepository.findById(f.getFriendId())
                            .orElseThrow(() -> new ResourceNotFoundException("Friend record missing"));
                    return new FriendResponse(friend.getId(), friend.getStudentId(), friend.getName(), friend.getSemester());
                })
                .toList();
    }
}
