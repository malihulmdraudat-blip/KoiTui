package com.freeslot.backend.controller;

import com.freeslot.backend.dto.FriendRequest;
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

    @PostMapping("/add")
    public FriendResponse addFriend(Authentication authentication, @Valid @RequestBody FriendRequest request) {
        return friendService.addFriend(authentication.getName(), request);
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
