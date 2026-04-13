export interface FeedbackResponse {
  id: number;
  answerId: number;
  questionContent: string;
  userAnswer: string;
  score: number;
  strengths: string[];
  weaknesses: string[];
  improvedAnswer: string;
}