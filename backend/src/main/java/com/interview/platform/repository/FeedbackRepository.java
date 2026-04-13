package com.interview.platform.repository;

import com.interview.platform.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByAnswerId(Long answerId);
}