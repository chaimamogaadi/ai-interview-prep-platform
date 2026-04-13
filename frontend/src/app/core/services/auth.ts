import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'jwt_token';
  private usernameKey = 'username';

  isLoggedIn$ = new BehaviorSubject<boolean>(this.hasToken());

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usernameKey);
    this.isLoggedIn$.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.usernameKey);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  private handleAuth(res: AuthResponse): void {
    localStorage.setItem(this.tokenKey, res.token);
    localStorage.setItem(this.usernameKey, res.username);
    this.isLoggedIn$.next(true);
  }
}