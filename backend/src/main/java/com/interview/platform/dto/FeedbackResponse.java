package com.interview.platform.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private Long id;
    private Long answerId;
    private String questionContent;
    private String userAnswer;
    private Integer score;
    private List<String> strengths;
    private List<String> weaknesses;
    private String improvedAnswer;
}