package com.candy.InterviewCoachAI.dto;


import com.candy.InterviewCoachAI.entity.Question;
import com.candy.InterviewCoachAI.entity.QuestionCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionResponse {
    private Long id;
    private String text;
    private QuestionCategory category;
    private int orderIndex;
    private AnswerResponse answer; // null if not yet answered

    public static QuestionResponse fromEntity(Question question) {
        QuestionResponse dto = new QuestionResponse();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setCategory(question.getCategory());
        dto.setOrderIndex(question.getOrderIndex());
        if (question.getAnswer() != null) {
            dto.setAnswer(AnswerResponse.fromEntity(question.getAnswer()));
        }
        return dto;
    }
}
