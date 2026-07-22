package com.candy.InterviewCoachAI.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionCreateRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 200)
    private String jobTitle;

    @NotBlank(message = "Job description is required")
    @Size(max = 5000)
    private String jobDescription;
}
