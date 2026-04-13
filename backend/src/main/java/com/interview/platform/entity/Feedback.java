package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "improved_answer", columnDefinition = "TEXT")
    private String improvedAnswer;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}