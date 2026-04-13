import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { InterviewResponse } from '../../models/interview.model';
import { AuthService } from '../../core/services/auth';
import { InterviewService } from '../../core/services/interview';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterLink, DatePipe,
    MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatProgressSpinnerModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  private interviewService = inject(InterviewService);
  private router = inject(Router);
  authService = inject(AuthService);

  interviews: InterviewResponse[] = [];
  loading = true;

  get completedCount(): number {
    return this.interviews.filter(i => i.status === 'COMPLETED').length;
  }

  ngOnInit(): void {
    this.interviewService.getUserInterviews().subscribe({
      next: (data) => { this.interviews = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  startNew(): void {
    this.router.navigate(['/interview']);
  }

  viewResults(id: number): void {
    this.router.navigate(['/results', id]);
  }

  getStatusColor(status: string): 'primary' | 'accent' {
    return status === 'COMPLETED' ? 'primary' : 'accent';
  }
}