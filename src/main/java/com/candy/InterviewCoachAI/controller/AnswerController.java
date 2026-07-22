package com.candy.InterviewCoachAI.controller;


import com.candy.InterviewCoachAI.dto.AnswerCreateRequest;
import com.candy.InterviewCoachAI.dto.AnswerResponse;
import com.candy.InterviewCoachAI.entity.Answer;
import com.candy.InterviewCoachAI.security.CurrentUser;
import com.candy.InterviewCoachAI.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final CurrentUser currentUser;

    @PostMapping("/{questionId}/answers")
    public AnswerResponse submitAnswer(@PathVariable Long questionId,
                                       @Valid @RequestBody AnswerCreateRequest request,
                                       @AuthenticationPrincipal OAuth2User principal) {
        String email = currentUser.emailOf(principal);
        Answer answer = answerService.submitAnswer(questionId, request, email);
        return AnswerResponse.fromEntity(answer);
    }
}