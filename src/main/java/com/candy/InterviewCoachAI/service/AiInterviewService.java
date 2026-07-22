package com.candy.InterviewCoachAI.service;

import com.candy.InterviewCoachAI.entity.Answer;
import com.candy.InterviewCoachAI.entity.QuestionCategory;
import com.candy.InterviewCoachAI.entity.WeaknessTag;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: This talks to Ollama directly via RestTemplate instead of Spring AI's
 * ChatClient/OllamaChatOptions. Spring AI 1.1.0 has a known bug where
 * disableThinking() / think(false) either doesn't get sent correctly or
 * causes a NullPointerException on ChatResponse.message() being null
 * (see spring-ai issues #4781 and #5128). Calling Ollama's /api/chat
 * endpoint directly with "think": false, which we confirmed works via
 * manual curl/PowerShell testing, avoids that bug entirely.
 */
@Service
@Slf4j
public class AiInterviewService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    private String callOllama(String prompt) {
        String url = ollamaBaseUrl + "/api/chat";

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", List.of(message));
        body.put("stream", false);
        body.put("think", false);

        long start = System.currentTimeMillis();
        String rawJson = restTemplate.postForObject(url, body, String.class);
        long elapsedMs = System.currentTimeMillis() - start;

        String content = "";
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            content = root.path("message").path("content").asText("");
        } catch (Exception e) {
            log.error("Failed to parse Ollama response JSON: {}", rawJson, e);
        }

        log.info("Ollama responded in {} ms. Raw content: [{}]", elapsedMs, content);
        return content;
    }

    public List<SessionService.GeneratedQuestion> generateQuestions(String jobTitle, String jobDescription) {
        String prompt = """
                You are helping someone prepare for a job interview.
                Job title: %s
                Job description: %s

                Generate exactly 6 interview questions this candidate is likely
                to be asked: a mix of BEHAVIORAL, TECHNICAL, SITUATIONAL, and
                CULTURE_FIT questions (at least one of each).

                Respond with ONLY the questions, one per line, in this exact format,
                no numbering, no extra commentary:
                CATEGORY: question text

                Example line:
                BEHAVIORAL: Tell me about a time you disagreed with a teammate.
                """.formatted(jobTitle, jobDescription);

        log.info("Calling Ollama for question generation...");
        String raw = callOllama(prompt);

        List<SessionService.GeneratedQuestion> questions = new ArrayList<>();
        int order = 0;
        for (String line : raw.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || !line.contains(":")) continue;

            String[] parts = line.split(":", 2);
            String categoryRaw = parts[0].trim().toUpperCase();
            String text = parts[1].trim();
            if (text.isEmpty()) continue;

            QuestionCategory category;
            try {
                category = QuestionCategory.valueOf(categoryRaw);
            } catch (IllegalArgumentException e) {
                category = QuestionCategory.BEHAVIORAL;
            }

            questions.add(new SessionService.GeneratedQuestion(text, category, order++));
        }

        if (questions.isEmpty()) {
            log.warn("AI question generation returned unparsable output, raw response: {}", raw);
        }

        return questions;
    }

    public AnswerCritique critiqueAnswer(String questionText, String answerText) {
        String tagOptions = Arrays.stream(WeaknessTag.values())
                .filter(t -> t != WeaknessTag.NONE)
                .map(Enum::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        String prompt = """
                You are an experienced interview coach. Critique this answer honestly
                but constructively.

                Question: %s
                Candidate's answer: %s

                Respond in EXACTLY this format, three lines, nothing else:
                SCORE: <a number from 1 to 10>
                FEEDBACK: <2-3 sentences of specific, actionable feedback>
                TAGS: <comma-separated list of weaknesses from this set: %s — or NONE if there are no notable weaknesses>
                """.formatted(questionText, answerText, tagOptions);

        log.info("Calling Ollama for answer critique...");
        String raw = callOllama(prompt);

        int score = 5;
        String feedback = "Feedback unavailable — please try again.";
        List<WeaknessTag> tags = new ArrayList<>();

        for (String line : raw.split("\n")) {
            line = line.trim();
            if (line.toUpperCase().startsWith("SCORE:")) {
                try {
                    score = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                } catch (NumberFormatException ignored) { }
            } else if (line.toUpperCase().startsWith("FEEDBACK:")) {
                feedback = line.substring(line.indexOf(":") + 1).trim();
            } else if (line.toUpperCase().startsWith("TAGS:")) {
                String tagsRaw = line.substring(line.indexOf(":") + 1).trim();
                for (String tagStr : tagsRaw.split(",")) {
                    tagStr = tagStr.trim().toUpperCase();
                    if (tagStr.isEmpty() || tagStr.equals("NONE")) continue;
                    try {
                        tags.add(WeaknessTag.valueOf(tagStr));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
        }

        score = Math.max(1, Math.min(10, score));
        return new AnswerCritique(score, feedback, tags);
    }

    public String generateProgressInsight(List<Answer> pastAnswers, Map<String, Long> weaknessCounts) {
        if (pastAnswers.isEmpty()) {
            return "No answers yet — complete a session to start building your progress history.";
        }

        StringBuilder sampleFeedback = new StringBuilder();
        pastAnswers.stream()
                .filter(a -> a.getAiFeedback() != null)
                .limit(10)
                .forEach(a -> sampleFeedback.append("- ").append(a.getAiFeedback()).append("\n"));

        String prompt = """
                You are an interview coach reviewing a candidate's history across
                multiple practice sessions. Here is how often each weakness has
                been flagged in their past answers:
                %s

                Here is a sample of the individual feedback given on past answers:
                %s

                Write a short, specific paragraph (3-4 sentences) identifying the
                candidate's most important recurring pattern. Be direct and
                concrete — reference the kind of situation where it shows up
                (e.g. behavioral questions about conflict) rather than being
                generic. If the data is too sparse to find a real pattern, say so
                honestly instead of inventing one.
                """.formatted(weaknessCounts.toString(), sampleFeedback);

        log.info("Calling Ollama for progress insight...");
        String raw = callOllama(prompt);
        return raw.trim();
    }

    public record AnswerCritique(int score, String feedback, List<WeaknessTag> tags) {}
}