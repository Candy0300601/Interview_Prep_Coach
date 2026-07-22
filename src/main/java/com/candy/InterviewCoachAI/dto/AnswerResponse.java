package com.candy.InterviewCoachAI.dto;



import com.candy.InterviewCoachAI.entity.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class AnswerResponse {
    private Long id;
    private String answerText;
    private String aiFeedback;
    private Integer score;
    private List<String> weaknessTags;
    private LocalDateTime createdAt;

    public static AnswerResponse fromEntity(Answer answer) {
        AnswerResponse dto = new AnswerResponse();
        dto.setId(answer.getId());
        dto.setAnswerText(answer.getAnswerText());
        dto.setAiFeedback(answer.getAiFeedback());
        dto.setScore(answer.getScore());
        String csv = answer.getWeaknessTagsCsv();
        dto.setWeaknessTags(
                csv == null || csv.isBlank() ? List.of() : Arrays.asList(csv.split(","))
        );
        dto.setCreatedAt(answer.getCreatedAt());
        return dto;
    }
}
