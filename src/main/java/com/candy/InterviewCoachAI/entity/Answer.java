package com.candy.InterviewCoachAI.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3000, nullable = false)
    private String answerText;

    @Column(nullable = false)
    private String userEmail;

    @Column(length = 2000)
    private String aiFeedback;

    private Integer score; // 1-10, null until AI critiques it

    /**
     * Stored as a simple comma-separated string of WeaknessTag names
     * (e.g. "VAGUE_ANSWER,TOO_SHORT") rather than a separate join table.
     * Simpler to reason about for a first project; a cleaner JPA approach
     * would use @ElementCollection, which is a good "v2" improvement.
     */
    private String weaknessTagsCsv;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    @JsonIgnore
    private Question question;
}

