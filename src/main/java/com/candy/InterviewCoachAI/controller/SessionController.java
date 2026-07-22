package com.candy.InterviewCoachAI.controller;


import com.candy.InterviewCoachAI.dto.SessionCreateRequest;
import com.candy.InterviewCoachAI.dto.SessionResponse;
import com.candy.InterviewCoachAI.entity.InterviewSession;
import com.candy.InterviewCoachAI.security.CurrentUser;
import com.candy.InterviewCoachAI.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final CurrentUser currentUser;

    @PostMapping
    public SessionResponse createSession(@Valid @RequestBody SessionCreateRequest request,
                                         @AuthenticationPrincipal OAuth2User principal) {
        String email = currentUser.emailOf(principal);
        InterviewSession session = sessionService.createSession(request, email);
        return SessionResponse.fromEntity(session);
    }

    @GetMapping("/mine")
    public List<SessionResponse> getMySessions(@AuthenticationPrincipal OAuth2User principal) {
        String email = currentUser.emailOf(principal);
        return sessionService.getSessionsForUser(email).stream()
                .map(SessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SessionResponse getSession(@PathVariable Long id) {
        InterviewSession session = sessionService.getSessionById(id);
        return SessionResponse.fromEntity(session);
    }
}