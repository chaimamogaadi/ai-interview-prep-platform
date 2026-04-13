package com.interview.platform.service;

import com.interview.platform.dto.*;
import com.interview.platform.entity.*;
import com.interview.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final OllamaService ollamaService;

    public InterviewResponse startInterview(InterviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Interview interview = Interview.builder()
                .user(user)
                .jobRole(request.getJobRole())
                .experienceLevel(request.getExperienceLevel())
                .status("IN_PROGRESS")
                .build();

        interview = interviewRepository.save(interview);

        // Generate questions via Ollama
        List<String> questionTexts = ollamaService.generateQuestions(
                request.getJobRole(), request.getExperienceLevel());

        AtomicInteger order = new AtomicInteger(0);
        Interview finalInterview = interview;
        List<Question> questions = questionTexts.stream().map(text -> {
            Question q = Question.builder()
                    .interview(finalInterview)
                    .content(text)
                    .questionOrder(order.getAndIncrement())
                    .build();
            return questionRepository.save(q);
        }).collect(Collectors.toList());

        return mapToResponse(interview, questions);
    }

    public List<InterviewResponse> getUserInterviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return interviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(interview -> {
                    List<Question> questions = questionRepository
                            .findByInterviewIdOrderByQuestionOrder(interview.getId());
                    return mapToResponse(interview, questions);
                })
                .collect(Collectors.toList());
    }

    public InterviewResponse getInterview(Long id, String username) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        if (!interview.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        List<Question> questions = questionRepository
                .findByInterviewIdOrderByQuestionOrder(interview.getId());

        return mapToResponse(interview, questions);
    }

    public void completeInterview(Long id, String username) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        if (!interview.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        interview.setStatus("COMPLETED");
        interviewRepository.save(interview);
    }

    private InterviewResponse mapToResponse(Interview interview, List<Question> questions) {
        List<InterviewResponse.QuestionDto> questionDtos = questions.stream()
                .map(q -> InterviewResponse.QuestionDto.builder()
                        .id(q.getId())
                        .content(q.getContent())
                        .questionOrder(q.getQuestionOrder())
                        .build())
                .collect(Collectors.toList());

        return InterviewResponse.builder()
                .id(interview.getId())
                .jobRole(interview.getJobRole())
                .experienceLevel(interview.getExperienceLevel())
                .status(interview.getStatus())
                .createdAt(interview.getCreatedAt())
                .questions(questionDtos)
                .build();
    }
}