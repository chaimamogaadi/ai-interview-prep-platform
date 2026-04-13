package com.interview.platform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private Long id;
    private Long questionId;
    private String questionContent;
    private String content;
    private LocalDateTime submittedAt;
}