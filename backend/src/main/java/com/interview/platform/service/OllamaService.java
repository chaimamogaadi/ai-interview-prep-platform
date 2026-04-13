package com.interview.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class OllamaService {

    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    // ─── Generate Questions ───────────────────────────────────────────────────

    public List<String> generateQuestions(String jobRole, String experienceLevel) {
        String prompt = "Give me 5 interview questions for a " + experienceLevel
                + " " + jobRole + " developer. Number them 1 to 5.";

        log.info("=== GENERATING QUESTIONS for {} {} ===", experienceLevel, jobRole);
        String response = callOllama(prompt);
        log.info("=== OLLAMA QUESTIONS RESPONSE ===\n{}", response);

        List<String> questions = parseNumberedList(response);
        log.info("=== PARSED {} QUESTIONS ===", questions.size());

        return questions.isEmpty() ? fallbackQuestions() : questions;
    }

    // ─── Evaluate Answer ──────────────────────────────────────────────────────

    public Map<String, Object> evaluateAnswer(String question, String userAnswer) {
        log.info("=== EVALUATING ANSWER ===");
        log.info("Question: {}", question);
        log.info("Answer: {}", userAnswer);

        int score = fetchScore(question, userAnswer);
        log.info("Score fetched: {}", score);

        List<String> strengths = fetchList(question, userAnswer, "strengths");
        log.info("Strengths: {}", strengths);

        List<String> weaknesses = fetchList(question, userAnswer, "weaknesses");
        log.info("Weaknesses: {}", weaknesses);

        String improved = fetchImproved(question, userAnswer);
        log.info("Improved answer length: {}", improved.length());

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("strengths", strengths);
        result.put("weaknesses", weaknesses);
        result.put("improved_answer", improved);

        log.info("=== FINAL RESULT: {} ===", result);
        return result;
    }

    // ─── Fetch Score ──────────────────────────────────────────────────────────

    private int fetchScore(String question, String answer) {
        String prompt = "Score this answer from 0 to 100. Reply with one number only.\n"
                + "Question: " + question + "\nAnswer: " + answer;

        String raw = callOllama(prompt);
        log.info("Raw score response: [{}]", raw);

        // Extract any number 0-100 from the response
        if (raw != null) {
            // Try to find patterns like "75", "Score: 75", "75/100", "75%"
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\\b([0-9]{1,3})\\b")
                    .matcher(raw);
            while (m.find()) {
                try {
                    int val = Integer.parseInt(m.group(1));
                    if (val >= 0 && val <= 100) {
                        log.info("Extracted score: {}", val);
                        return val;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        log.warn("Could not extract score, using default 50");
        return 50;
    }

    // ─── Fetch Strengths or Weaknesses ────────────────────────────────────────

    private List<String> fetchList(String question, String answer, String type) {
        String prompt;
        if (type.equals("strengths")) {
            prompt = "List 2 good things about this interview answer. "
                    + "Use format:\n- point one\n- point two\n\n"
                    + "Question: " + question + "\nAnswer: " + answer;
        } else {
            prompt = "List 2 things to improve in this interview answer. "
                    + "Use format:\n- point one\n- point two\n\n"
                    + "Question: " + question + "\nAnswer: " + answer;
        }

        String raw = callOllama(prompt);
        log.info("Raw {} response: [{}]", type, raw);

        List<String> result = new ArrayList<>();

        if (raw != null) {
            for (String line : raw.split("\n")) {
                line = line.trim();
                // Accept lines starting with -, *, •, or numbers like "1."
                if (line.matches("^[-*•].*") || line.matches("^[0-9]+[.)].+")) {
                    String cleaned = line.replaceFirst("^[-*•0-9.)]+\\s*", "").trim();
                    if (cleaned.length() > 3) {
                        result.add(cleaned);
                    }
                }
            }
        }

        // If still nothing, grab any non-empty lines
        if (result.isEmpty() && raw != null) {
            for (String line : raw.split("\n")) {
                line = line.trim();
                if (line.length() > 10 && !line.toLowerCase().contains("question")
                        && !line.toLowerCase().contains("answer")) {
                    result.add(line);
                    if (result.size() == 2) break;
                }
            }
        }

        if (result.isEmpty()) {
            result = type.equals("strengths")
                    ? List.of("Attempted to answer the question", "Shows basic understanding")
                    : List.of("Could provide more detail", "Consider adding examples");
        }

        return result;
    }

    // ─── Fetch Improved Answer ────────────────────────────────────────────────

    private String fetchImproved(String question, String answer) {
        String prompt = "Write a better version of this interview answer in 2-3 sentences.\n"
                + "Question: " + question + "\nOriginal answer: " + answer
                + "\nBetter answer:";

        String raw = callOllama(prompt);
        log.info("Raw improved response: [{}]", raw);

        if (raw == null || raw.trim().isEmpty()) {
            return "A stronger answer would include specific examples, "
                    + "technical details, and demonstrate practical experience with the topic.";
        }

        // Remove any prefix like "Better answer:" if the model echoed it
        String cleaned = raw.trim()
                .replaceFirst("(?i)^better answer[:\\s]*", "")
                .replaceFirst("(?i)^improved answer[:\\s]*", "")
                .trim();

        return cleaned.isEmpty() ? raw.trim() : cleaned;
    }

    // ─── Parse numbered list ──────────────────────────────────────────────────

    private List<String> parseNumberedList(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        for (String line : text.split("\n")) {
            line = line.trim();
            // Match "1. text", "1) text", "1 - text"
            if (line.matches("^[1-9][.)\\-]\\s*.+")) {
                String q = line.replaceFirst("^[1-9][.)\\-]\\s*", "").trim();
                if (q.length() > 5) result.add(q);
            }
        }

        // Fallback: grab lines ending with "?"
        if (result.size() < 3) {
            result.clear();
            for (String line : text.split("\n")) {
                line = line.trim();
                if (line.endsWith("?") && line.length() > 10) {
                    result.add(line);
                    if (result.size() == 5) break;
                }
            }
        }

        return result;
    }

    // ─── Fallback Questions ───────────────────────────────────────────────────

    private List<String> fallbackQuestions() {
        return List.of(
                "Explain the difference between REST and GraphQL.",
                "What is dependency injection and why is it useful?",
                "How do you handle errors in your applications?",
                "Describe your experience with version control systems like Git.",
                "What is your approach to writing unit tests?"
        );
    }

    // ─── Call Ollama ──────────────────────────────────────────────────────────

    private String callOllama(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.2);
            options.put("num_predict", 200);
            options.put("stop", List.of("\n\n\n", "Question:", "Answer:"));

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("prompt", prompt);
            body.put("stream", false);
            body.put("options", options);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    ollamaUrl, request, Map.class);

            if (response.getBody() != null
                    && response.getBody().containsKey("response")) {
                return response.getBody().get("response").toString();
            }
        } catch (Exception e) {
            log.error("Ollama API call failed: {}", e.getMessage());
        }
        return "";
    }
}