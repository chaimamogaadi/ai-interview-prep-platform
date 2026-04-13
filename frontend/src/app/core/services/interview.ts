import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InterviewRequest, InterviewResponse } from '../../models/interview.model';

@Injectable({ providedIn: 'root' })
export class InterviewService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/interview';

  startInterview(request: InterviewRequest): Observable<InterviewResponse> {
    return this.http.post<InterviewResponse>(`${this.apiUrl}/start`, request);
  }

  getUserInterviews(): Observable<InterviewResponse[]> {
    return this.http.get<InterviewResponse[]>(this.apiUrl);
  }

  getInterview(id: number): Observable<InterviewResponse> {
    return this.http.get<InterviewResponse>(`${this.apiUrl}/${id}`);
  }

  completeInterview(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/complete`, {});
  }
}