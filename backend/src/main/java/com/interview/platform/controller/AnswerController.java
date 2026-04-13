package com.interview.platform.controller;

import com.interview.platform.dto.*;
import com.interview.platform.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/submit")
    public ResponseEntity<AnswerResponse> submitAnswer(
            @Valid @RequestBody AnswerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(answerService.submitAnswer(request, userDetails.getUsername()));
    }

    @GetMapping("/interview/{interviewId}")
    public ResponseEntity<List<AnswerResponse>> getAnswers(@PathVariable Long interviewId) {
        return ResponseEntity.ok(answerService.getInterviewAnswers(interviewId));
    }
}