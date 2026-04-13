package com.interview.platform.repository;

import com.interview.platform.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByInterviewId(Long interviewId);
    List<Answer> findByUserId(Long userId);
}