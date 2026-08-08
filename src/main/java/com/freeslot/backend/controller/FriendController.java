package com.freeslot.backend.controller;

import com.freeslot.backend.dto.FriendRequest;
import com.freeslot.backend.dto.FriendRequestResponse;
import com.freeslot.backend.dto.FriendResponse;
import com.freeslot.backend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // Sends a friend request (no longer adds instantly - the other user must accept it).
    @PostMapping("/add")
    public FriendRequestResponse addFriend(Authentication authentication, @Valid @RequestBody FriendRequest request) {
        return friendService.sendFriendRequest(authentication.getName(), request);
    }

    // Pending friend requests sent TO the current user.
    @GetMapping("/requests")
    public List<FriendRequestResponse> getIncomingRequests(Authentication authentication) {
        return friendService.getIncomingRequests(authentication.getName());
    }

    @PostMapping("/requests/{requestId}/accept")
    public FriendResponse acceptRequest(Authentication authentication, @PathVariable Long requestId) {
        return friendService.acceptRequest(authentication.getName(), requestId);
    }

    @PostMapping("/requests/{requestId}/reject")
    public void rejectRequest(Authentication authentication, @PathVariable Long requestId) {
        friendService.rejectRequest(authentication.getName(), requestId);
    }

    @DeleteMapping("/remove/{friendId}")
    public void removeFriend(Authentication authentication, @PathVariable Long friendId) {
        friendService.removeFriend(authentication.getName(), friendId);
    }

    @GetMapping
    public List<FriendResponse> getFriends(Authentication authentication) {
        return friendService.getMyFriends(authentication.getName());
    }
}
