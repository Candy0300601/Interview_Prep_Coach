package com.candy.InterviewCoachAI.service;


import com.candy.InterviewCoachAI.dto.ProgressReportResponse;
import com.candy.InterviewCoachAI.entity.Answer;
import com.candy.InterviewCoachAI.entity.WeaknessTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final AnswerService answerService;
    private final AiInterviewService aiInterviewService;

    public ProgressReportResponse generateReport(String userEmail) {
        List<Answer> allAnswers = answerService.getAllAnswersForUser(userEmail);

        Map<String, Long> tagCounts = countWeaknessTags(allAnswers);

        double averageScore = allAnswers.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(Answer::getScore)
                .average()
                .orElse(0.0);

        List<String> topWeaknesses = tagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String narrative = aiInterviewService.generateProgressInsight(allAnswers, tagCounts);

        return new ProgressReportResponse(
                allAnswers.size(),
                Math.round(averageScore * 10.0) / 10.0,
                tagCounts,
                topWeaknesses,
                narrative
        );
    }

    private Map<String, Long> countWeaknessTags(List<Answer> answers) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Answer answer : answers) {
            String csv = answer.getWeaknessTagsCsv();
            if (csv == null || csv.isBlank()) continue;

            for (String tag : csv.split(",")) {
                if (tag.isBlank() || tag.equals(WeaknessTag.NONE.name())) continue;
                counts.merge(tag, 1L, Long::sum);
            }
        }
        return counts;
    }
}
