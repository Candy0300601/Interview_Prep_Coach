package com.candy.InterviewCoachAI.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ProgressReportResponse {
    private int totalAnswersAnalyzed;
    private double averageScore;
    private Map<String, Long> weaknessTagCounts; // e.g. {"VAGUE_ANSWER": 5, "TOO_SHORT": 3}
    private List<String> topWeaknesses;          // the 1-3 most frequent, non-trivial weaknesses
    private String narrativeInsight;              // AI-written, human-readable pattern summary
}
