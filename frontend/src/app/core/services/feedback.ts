import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedbackResponse } from '../../models/feedback.model';

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/feedback';

  generateFeedback(answerId: number): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(`${this.apiUrl}/generate/${answerId}`, {});
  }

  getInterviewFeedbacks(interviewId: number): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(`${this.apiUrl}/interview/${interviewId}`);
  }
}