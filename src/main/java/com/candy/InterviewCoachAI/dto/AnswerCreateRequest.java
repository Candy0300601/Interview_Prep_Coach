package com.candy.InterviewCoachAI.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerCreateRequest {

    @NotBlank(message = "Answer text is required")
    @Size(max = 3000)
    private String answerText;
}
