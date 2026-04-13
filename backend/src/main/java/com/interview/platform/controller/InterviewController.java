package com.interview.platform.controller;

import com.interview.platform.dto.*;
import com.interview.platform.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<InterviewResponse> startInterview(
            @Valid @RequestBody InterviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interviewService.startInterview(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getUserInterviews(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interviewService.getUserInterviews(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getInterview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interviewService.getInterview(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeInterview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        interviewService.completeInterview(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}