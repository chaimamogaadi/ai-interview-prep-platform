package com.interview.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotNull
    private Long questionId;
    @NotNull
    private Long interviewId;
    @NotBlank
    private String content;
}