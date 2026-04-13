package com.interview.platform.service;

import com.interview.platform.dto.FeedbackResponse;
import com.interview.platform.entity.*;
import com.interview.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AnswerRepository answerRepository;
    private final OllamaService ollamaService;

    public FeedbackResponse generateFeedback(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Check if feedback already exists
        Optional<Feedback> existing = feedbackRepository.findByAnswerId(answerId);
        if (existing.isPresent()) {
            return mapToResponse(existing.get(), answer);
        }

        // Call Ollama AI for evaluation
        Map<String, Object> result = ollamaService.evaluateAnswer(
                answer.getQuestion().getContent(),
                answer.getContent()
        );

        Feedback feedback = Feedback.builder()
                .answer(answer)
                .score(((Number) result.getOrDefault("score", 50)).intValue())
                .strengths(listToString((List<?>) result.getOrDefault("strengths", List.of())))
                .weaknesses(listToString((List<?>) result.getOrDefault("weaknesses", List.of())))
                .improvedAnswer((String) result.getOrDefault("improved_answer", ""))
                .build();

        feedback = feedbackRepository.save(feedback);
        return mapToResponse(feedback, answer);
    }

    public List<FeedbackResponse> getInterviewFeedbacks(Long interviewId) {
        return answerRepository.findByInterviewId(interviewId).stream()
                .map(answer -> feedbackRepository.findByAnswerId(answer.getId())
                        .map(f -> mapToResponse(f, answer))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String listToString(List<?> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining("||"));
    }

    private List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) return List.of();
        return Arrays.asList(str.split("\\|\\|"));
    }

    private FeedbackResponse mapToResponse(Feedback feedback, Answer answer) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .answerId(answer.getId())
                .questionContent(answer.getQuestion().getContent())
                .userAnswer(answer.getContent())
                .score(feedback.getScore())
                .strengths(stringToList(feedback.getStrengths()))
                .weaknesses(stringToList(feedback.getWeaknesses()))
                .improvedAnswer(feedback.getImprovedAnswer())
                .build();
    }
}