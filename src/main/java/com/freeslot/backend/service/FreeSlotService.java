package com.freeslot.backend.service;

import com.freeslot.backend.dto.FriendRequest;
import com.freeslot.backend.dto.FriendRequestResponse;
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

    private static final String PENDING = "PENDING";
    private static final String ACCEPTED = "ACCEPTED";

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // Sends a friend request instead of adding instantly. The recipient must
    // accept it (see acceptRequest) before the two become friends.
    public FriendRequestResponse sendFriendRequest(String email, FriendRequest request) {
        User user = userService.getUserByEmail(email);

        User target = userRepository.findByStudentId(request.getFriendStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("No student found with that student ID"));

        if (target.getId().equals(user.getId())) {
            throw new BadRequestException("You cannot add yourself as a friend");
        }

        friendRepository.findByUserIdAndFriendId(user.getId(), target.getId()).ifPresent(f -> {
            if (ACCEPTED.equals(f.getStatus())) {
                throw new BadRequestException("This user is already in your friend list");
            }
            throw new BadRequestException("You already sent a friend request to this user");
        });

        friendRepository.findByUserIdAndFriendId(target.getId(), user.getId()).ifPresent(f -> {
            if (ACCEPTED.equals(f.getStatus())) {
                throw new BadRequestException("This user is already in your friend list");
            }
            throw new BadRequestException("This user already sent you a friend request - check your friend requests");
        });

        Friend friendRequestEntry = new Friend();
        friendRequestEntry.setUserId(user.getId());
        friendRequestEntry.setFriendId(target.getId());
        friendRequestEntry.setStatus(PENDING);
        Friend saved = friendRepository.save(friendRequestEntry);

        return new FriendRequestResponse(saved.getId(), user.getId(), target.getStudentId(),
                target.getName(), target.getSemester());
    }

    // Friend requests sent TO the current user that are still awaiting a response.
    public List<FriendRequestResponse> getIncomingRequests(String email) {
        User user = userService.getUserByEmail(email);

        return friendRepository.findByFriendIdAndStatus(user.getId(), PENDING).stream()
                .map(f -> {
                    User requester = userRepository.findById(f.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("Requester account missing"));
                    return new FriendRequestResponse(f.getId(), requester.getId(), requester.getStudentId(),
                            requester.getName(), requester.getSemester());
                })
                .toList();
    }

    @Transactional
    public FriendResponse acceptRequest(String email, Long requestId) {
        User user = userService.getUserByEmail(email);

        Friend request = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        if (!request.getFriendId().equals(user.getId()) || !PENDING.equals(request.getStatus())) {
            throw new BadRequestException("This friend request is no longer valid");
        }

        request.setStatus(ACCEPTED);
        friendRepository.save(request);

        // Mirror the relationship so it shows up in both users' friend lists.
        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester account missing"));

        Friend reciprocal = friendRepository.findByUserIdAndFriendId(user.getId(), requester.getId())
                .orElseGet(Friend::new);
        reciprocal.setUserId(user.getId());
        reciprocal.setFriendId(requester.getId());
        reciprocal.setStatus(ACCEPTED);
        friendRepository.save(reciprocal);

        return new FriendResponse(requester.getId(), requester.getStudentId(), requester.getName(), requester.getSemester());
    }

    @Transactional
    public void rejectRequest(String email, Long requestId) {
        User user = userService.getUserByEmail(email);

        Friend request = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        if (!request.getFriendId().equals(user.getId()) || !PENDING.equals(request.getStatus())) {
            throw new BadRequestException("This friend request is no longer valid");
        }

        friendRepository.delete(request);
    }

    @Transactional
    public void removeFriend(String email, Long friendId) {
        User user = userService.getUserByEmail(email);
        friendRepository.deleteByUserIdAndFriendId(user.getId(), friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, user.getId());
    }

    public List<FriendResponse> getMyFriends(String email) {
        User user = userService.getUserByEmail(email);

        return friendRepository.findByUserIdAndStatus(user.getId(), ACCEPTED).stream()
                .map(f -> {
                    User friend = userRepository.findById(f.getFriendId())
                            .orElseThrow(() -> new ResourceNotFoundException("Friend record missing"));
                    return new FriendResponse(friend.getId(), friend.getStudentId(), friend.getName(), friend.getSemester());
                })
                .toList();
    }
}
