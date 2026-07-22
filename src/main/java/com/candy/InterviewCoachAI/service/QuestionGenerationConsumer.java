package com.candy.InterviewCoachAI.service;


import com.candy.InterviewCoachAI.dto.SessionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionGenerationConsumer {

    private final AiInterviewService aiInterviewService;
    private final SessionService sessionService;

    @KafkaListener(topics = "session-created", groupId = "session-question-generator")
    public void handleSessionCreated(SessionCreatedEvent event) {
        log.info("Generating questions for session {}", event.getSessionId());

        try {
            List<SessionService.GeneratedQuestion> questions =
                    aiInterviewService.generateQuestions(event.getJobTitle(), event.getJobDescription());

            sessionService.attachGeneratedQuestions(event.getSessionId(), questions);

            log.info("Generated {} questions for session {}", questions.size(), event.getSessionId());
        } catch (Exception e) {
            log.error("Question generation failed for session {}: {}", event.getSessionId(), e.getMessage());
            sessionService.markFailed(event.getSessionId());
        }
    }
}