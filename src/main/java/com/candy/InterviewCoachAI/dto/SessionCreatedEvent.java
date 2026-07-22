package com.candy.InterviewCoachAI.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreatedEvent {
    private Long sessionId;
    private String jobTitle;
    private String jobDescription;
}
