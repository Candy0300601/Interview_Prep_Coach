package com.candy.InterviewCoachAI.dto;


import com.candy.InterviewCoachAI.entity.InterviewSession;
import com.candy.InterviewCoachAI.entity.SessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SessionResponse {
    private Long id;
    private String jobTitle;
    private String jobDescription;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private List<QuestionResponse> questions;

    public static SessionResponse fromEntity(InterviewSession session) {
        SessionResponse dto = new SessionResponse();
        dto.setId(session.getId());
        dto.setJobTitle(session.getJobTitle());
        dto.setJobDescription(session.getJobDescription());
        dto.setStatus(session.getStatus());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setQuestions(
                session.getQuestions().stream()
                        .map(QuestionResponse::fromEntity)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
