import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FeedbackResponse } from '../../models/feedback.model';
import { AnswerResponse } from '../../models/answer.model';
import { FeedbackService } from '../../core/services/feedback';
import { AnswerService } from '../../core/services/answer';

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [
    RouterLink,
    MatCardModule, MatButtonModule, MatIconModule,
    MatProgressBarModule, MatProgressSpinnerModule
  ],
  templateUrl: './results.html',
  styleUrl: './results.scss'
})
export class ResultsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private feedbackService = inject(FeedbackService);
  private answerService = inject(AnswerService);

  interviewId!: number;
  answers: AnswerResponse[] = [];
  feedbacks: FeedbackResponse[] = [];
  loadingFeedback = false;
  feedbackGenerated = false;
  averageScore = 0;

  ngOnInit(): void {
    this.interviewId = Number(this.route.snapshot.paramMap.get('interviewId'));
    this.loadAnswers();
  }

  loadAnswers(): void {
    this.answerService.getInterviewAnswers(this.interviewId).subscribe({
      next: (answers) => {
        this.answers = answers;
        this.loadExistingFeedback();
      }
    });
  }

  loadExistingFeedback(): void {
    this.feedbackService.getInterviewFeedbacks(this.interviewId).subscribe({
      next: (feedbacks) => {
        if (feedbacks.length > 0) {
          this.feedbacks = feedbacks;
          this.feedbackGenerated = true;
          this.calculateAverage();
        }
      }
    });
  }

  generateAllFeedback(): void {
    if (this.answers.length === 0) return;
    this.loadingFeedback = true;

    forkJoin(this.answers.map(a => this.feedbackService.generateFeedback(a.id))).subscribe({
      next: (feedbacks) => {
        this.feedbacks = feedbacks;
        this.feedbackGenerated = true;
        this.loadingFeedback = false;
        this.calculateAverage();
      },
      error: () => { this.loadingFeedback = false; }
    });
  }

  calculateAverage(): void {
    if (!this.feedbacks.length) return;
    const total = this.feedbacks.reduce((sum, f) => sum + f.score, 0);
    this.averageScore = Math.round(total / this.feedbacks.length);
  }

  getScoreColor(score: number): string {
    if (score >= 80) return '#4caf50';
    if (score >= 60) return '#ff9800';
    return '#f44336';
  }
}