package com.interview.platform.service;

import com.interview.platform.dto.*;
import com.interview.platform.entity.*;
import com.interview.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    public AnswerResponse submitAnswer(AnswerRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        Answer answer = Answer.builder()
                .question(question)
                .interview(interview)
                .user(user)
                .content(request.getContent())
                .build();

        answer = answerRepository.save(answer);

        return AnswerResponse.builder()
                .id(answer.getId())
                .questionId(question.getId())
                .questionContent(question.getContent())
                .content(answer.getContent())
                .submittedAt(answer.getSubmittedAt())
                .build();
    }

    public List<AnswerResponse> getInterviewAnswers(Long interviewId) {
        return answerRepository.findByInterviewId(interviewId).stream()
                .map(a -> AnswerResponse.builder()
                        .id(a.getId())
                        .questionId(a.getQuestion().getId())
                        .questionContent(a.getQuestion().getContent())
                        .content(a.getContent())
                        .submittedAt(a.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }
}