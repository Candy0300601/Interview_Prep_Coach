package com.candy.InterviewCoachAI.repository;


import com.candy.InterviewCoachAI.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
