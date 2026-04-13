import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnswerRequest, AnswerResponse } from '../../models/answer.model';

@Injectable({ providedIn: 'root' })
export class AnswerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/answer';

  submitAnswer(request: AnswerRequest): Observable<AnswerResponse> {
    return this.http.post<AnswerResponse>(`${this.apiUrl}/submit`, request);
  }

  getInterviewAnswers(interviewId: number): Observable<AnswerResponse[]> {
    return this.http.get<AnswerResponse[]>(`${this.apiUrl}/interview/${interviewId}`);
  }
}