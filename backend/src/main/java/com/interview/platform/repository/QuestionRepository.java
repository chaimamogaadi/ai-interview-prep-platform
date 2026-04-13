package com.interview.platform.repository;

import com.interview.platform.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByInterviewIdOrderByQuestionOrder(Long interviewId);
}