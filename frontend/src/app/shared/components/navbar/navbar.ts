import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, AsyncPipe, MatToolbarModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary">
      <span routerLink="/dashboard" style="cursor:pointer;font-weight:600">
        🎯 Interview Prep AI
      </span>
      <span style="flex:1"></span>
      @if (authService.isLoggedIn$ | async) {
        <span style="margin-right:16px">{{ authService.getUsername() }}</span>
        <button mat-stroked-button (click)="authService.logout()">Logout</button>
      }
    </mat-toolbar>
  `
})
export class NavbarComponent {
  authService = inject(AuthService);
}