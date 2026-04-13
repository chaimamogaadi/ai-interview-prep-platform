package com.interview.platform.controller;

import com.interview.platform.dto.FeedbackResponse;
import com.interview.platform.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/generate/{answerId}")
    public ResponseEntity<FeedbackResponse> generateFeedback(@PathVariable Long answerId) {
        return ResponseEntity.ok(feedbackService.generateFeedback(answerId));
    }

    @GetMapping("/interview/{interviewId}")
    public ResponseEntity<List<FeedbackResponse>> getInterviewFeedbacks(
            @PathVariable Long interviewId) {
        return ResponseEntity.ok(feedbackService.getInterviewFeedbacks(interviewId));
    }
}