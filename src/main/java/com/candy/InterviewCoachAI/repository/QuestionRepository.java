package com.candy.InterviewCoachAI.repository;


import com.candy.InterviewCoachAI.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySessionIdOrderByOrderIndexAsc(Long sessionId);
}

