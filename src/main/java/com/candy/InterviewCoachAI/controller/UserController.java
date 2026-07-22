package com.candy.InterviewCoachAI.controller;

import com.candy.InterviewCoachAI.entity.AppUser;
import com.candy.InterviewCoachAI.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final AppUserRepository appUserRepository;

    @GetMapping("/api/me")
    public AppUser me(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found in database"));
    }
}