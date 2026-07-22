package com.candy.InterviewCoachAI.service;


import com.candy.InterviewCoachAI.dto.AnswerCreateRequest;
import com.candy.InterviewCoachAI.entity.Answer;
import com.candy.InterviewCoachAI.entity.Question;
import com.candy.InterviewCoachAI.entity.WeaknessTag;
import com.candy.InterviewCoachAI.exception.ResourceNotFoundException;
import com.candy.InterviewCoachAI.repository.AnswerRepository;
import com.candy.InterviewCoachAI.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AiInterviewService aiInterviewService;

    @Transactional
    public Answer submitAnswer(Long questionId, AnswerCreateRequest request, String userEmail) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        AiInterviewService.AnswerCritique critique =
                aiInterviewService.critiqueAnswer(question.getText(), request.getAnswerText());

        Answer answer = new Answer();
        answer.setAnswerText(request.getAnswerText());
        answer.setUserEmail(userEmail);
        answer.setScore(critique.score());
        answer.setAiFeedback(critique.feedback());
        answer.setWeaknessTagsCsv(
                critique.tags().stream().map(WeaknessTag::name).collect(Collectors.joining(","))
        );
        answer.setQuestion(question);

        return answerRepository.save(answer);
    }

    public List<Answer> getAllAnswersForUser(String userEmail) {
        return answerRepository.findByUserEmailOrderByCreatedAtAsc(userEmail);
    }
}