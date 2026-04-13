export interface InterviewRequest {
  jobRole: string;
  experienceLevel: string;
}

export interface QuestionDto {
  id: number;
  content: string;
  questionOrder: number;
}

export interface InterviewResponse {
  id: number;
  jobRole: string;
  experienceLevel: string;
  status: string;
  createdAt: string;
  questions: QuestionDto[];
}