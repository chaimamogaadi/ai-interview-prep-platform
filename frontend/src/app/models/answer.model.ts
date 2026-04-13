export interface AnswerRequest {
  questionId: number;
  interviewId: number;
  content: string;
}

export interface AnswerResponse {
  id: number;
  questionId: number;
  questionContent: string;
  content: string;
  submittedAt: string;
}