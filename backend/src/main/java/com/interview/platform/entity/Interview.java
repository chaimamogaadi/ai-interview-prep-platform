package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @Column(name = "experience_level", nullable = false)
    private String experienceLevel;

    @Column
    private String status = "IN_PROGRESS";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}