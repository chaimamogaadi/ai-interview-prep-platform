import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { InterviewResponse, QuestionDto } from '../../models/interview.model';
import { AnswerService } from '../../core/services/answer';
import { InterviewService } from '../../core/services/interview';

@Component({
  selector: 'app-interview',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatProgressBarModule, MatProgressSpinnerModule, MatSnackBarModule
  ],
  templateUrl: './interview.html',
  styleUrl: './interview.scss'
})
export class InterviewComponent {
  private interviewService = inject(InterviewService);
  private answerService = inject(AnswerService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  step: 'setup' | 'interview' = 'setup';
  interview: InterviewResponse | null = null;

  jobRole = '';
  experienceLevel = '';
  jobRoles = ['Frontend', 'Backend', 'Fullstack'];
  levels = ['Junior', 'Mid-level', 'Senior'];

  currentQuestionIndex = 0;
  currentAnswer = '';
  loadingInterview = false;
  submittingAnswer = false;

  get currentQuestion(): QuestionDto | null {
    return this.interview?.questions[this.currentQuestionIndex] ?? null;
  }

  get progress(): number {
    if (!this.interview) return 0;
    return (this.currentQuestionIndex / this.interview.questions.length) * 100;
  }

  get isLastQuestion(): boolean {
    if (!this.interview) return false;
    return this.currentQuestionIndex === this.interview.questions.length - 1;
  }

  startInterview(): void {
    if (!this.jobRole || !this.experienceLevel) return;
    this.loadingInterview = true;

    this.interviewService.startInterview({
      jobRole: this.jobRole,
      experienceLevel: this.experienceLevel
    }).subscribe({
      next: (data) => {
        this.interview = data;
        this.step = 'interview';
        this.loadingInterview = false;
      },
      error: () => {
        this.snackBar.open('Failed to start interview. Is Ollama running?', 'Close', { duration: 4000 });
        this.loadingInterview = false;
      }
    });
  }

  submitCurrentAnswer(): void {
    if (!this.currentAnswer.trim() || !this.currentQuestion || !this.interview) return;
    this.submittingAnswer = true;

    this.answerService.submitAnswer({
      questionId: this.currentQuestion.id,
      interviewId: this.interview.id,
      content: this.currentAnswer
    }).subscribe({
      next: () => {
        this.currentAnswer = '';
        if (!this.isLastQuestion) {
          this.currentQuestionIndex++;
          this.submittingAnswer = false;
        } else {
          this.finishInterview();
        }
      },
      error: () => {
        this.snackBar.open('Failed to submit answer', 'Close', { duration: 3000 });
        this.submittingAnswer = false;
      }
    });
  }

  private finishInterview(): void {
    this.interviewService.completeInterview(this.interview!.id).subscribe({
      next: () => this.router.navigate(['/results', this.interview!.id]),
      error: () => this.router.navigate(['/results', this.interview!.id])
    });
  }
}