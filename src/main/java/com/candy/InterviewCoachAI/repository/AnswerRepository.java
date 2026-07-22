package com.candy.InterviewCoachAI.repository;



import com.candy.InterviewCoachAI.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /**
     * Every answer a user has ever given, across ALL sessions. This is the
     * query the progress/pattern-detection feature is built on top of.
     */
    List<Answer> findByUserEmailOrderByCreatedAtAsc(String userEmail);
}
