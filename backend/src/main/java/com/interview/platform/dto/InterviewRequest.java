package com.interview.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewRequest {
    @NotBlank
    private String jobRole;
    @NotBlank
    private String experienceLevel;
}