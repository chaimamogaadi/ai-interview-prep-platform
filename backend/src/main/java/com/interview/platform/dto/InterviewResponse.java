package com.interview.platform.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewResponse {
    private Long id;
    private String jobRole;
    private String experienceLevel;
    private String status;
    private LocalDateTime createdAt;
    private List<QuestionDto> questions;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionDto {
        private Long id;
        private String content;
        private Integer questionOrder;
    }
}