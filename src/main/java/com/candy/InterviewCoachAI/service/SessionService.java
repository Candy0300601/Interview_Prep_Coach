package com.candy.InterviewCoachAI.service;

import com.candy.InterviewCoachAI.dto.SessionCreateRequest;
import com.candy.InterviewCoachAI.dto.SessionCreatedEvent;
import com.candy.InterviewCoachAI.entity.InterviewSession;
import com.candy.InterviewCoachAI.entity.Question;
import com.candy.InterviewCoachAI.entity.SessionStatus;
import com.candy.InterviewCoachAI.exception.ResourceNotFoundException;
import com.candy.InterviewCoachAI.repository.InterviewSessionRepository;
import com.candy.InterviewCoachAI.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String SESSION_CREATED_TOPIC = "session-created";

    private final InterviewSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final KafkaTemplate<String, SessionCreatedEvent> kafkaTemplate;

    @Transactional
    public InterviewSession createSession(SessionCreateRequest request, String userEmail) {
        InterviewSession session = new InterviewSession();
        session.setJobTitle(request.getJobTitle());
        session.setJobDescription(request.getJobDescription());
        session.setUserEmail(userEmail);
        session.setStatus(SessionStatus.GENERATING); // was READY before - now we wait for the consumer

        InterviewSession saved = sessionRepository.save(session);

        kafkaTemplate.send(SESSION_CREATED_TOPIC,
                new SessionCreatedEvent(saved.getId(), saved.getJobTitle(), saved.getJobDescription()));

        return saved;
    }

    public InterviewSession getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
    }

    public List<InterviewSession> getSessionsForUser(String userEmail) {
        return sessionRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    /** Called by the Kafka consumer once it's generated questions (Phase 4/5). */
    @Transactional
    public void attachGeneratedQuestions(Long sessionId, List<GeneratedQuestion> generated) {
        InterviewSession session = getSessionById(sessionId);

        for (GeneratedQuestion gq : generated) {
            Question question = new Question();
            question.setText(gq.text());
            question.setCategory(gq.category());
            question.setOrderIndex(gq.orderIndex());
            question.setSession(session);
            questionRepository.save(question);
        }

        session.setStatus(generated.isEmpty() ? SessionStatus.FAILED : SessionStatus.READY);
        sessionRepository.save(session);
    }

    @Transactional
    public void markFailed(Long sessionId) {
        InterviewSession session = getSessionById(sessionId);
        session.setStatus(SessionStatus.FAILED);
        sessionRepository.save(session);
    }

    /** Simple holder used to pass generated questions around. Real AI fills this in Phase 5. */
    public record GeneratedQuestion(String text, com.candy.InterviewCoachAI.entity.QuestionCategory category, int orderIndex) {}
}