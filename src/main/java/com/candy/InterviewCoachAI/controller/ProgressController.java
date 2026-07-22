package com.candy.InterviewCoachAI.controller;


import com.candy.InterviewCoachAI.dto.ProgressReportResponse;
import com.candy.InterviewCoachAI.security.CurrentUser;
import com.candy.InterviewCoachAI.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final CurrentUser currentUser;

    @GetMapping("/report")
    public ProgressReportResponse getProgressReport(@AuthenticationPrincipal OAuth2User principal) {
        String email = currentUser.emailOf(principal);
        return progressService.generateReport(email);
    }
}
