import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../src/environments/environment';
import { LoginRequest } from '../models/login-request.model';
import { RegisterRequest } from '../models/register-request.model';
import { AuthResponse } from '../models/auth-response.model';
import { EmailVerificationRequest } from '../models/email-verification-request.model';
import { ForgotPasswordRequest } from '../models/forgot-password.model';
import { ResetPassword } from '../models/reset-password.model';
import { GoogleLoginRequest } from '../models/google-login-request.model';
import { ApiResponse } from '../../shared/models/api-response.model';
import { SessionResponse } from '../models/session-response.mode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth';

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request, {
      withCredentials: true,
    });
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request, {
      withCredentials: true,
    });
  }

  googleLogin(request: GoogleLoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/google-login`,
      request,
      {
        withCredentials: true,
      }
    );
  }

  logout(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/logout`,
      {},
      {
        withCredentials: true,
      }
    );
  }

  logoutAll(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/logout-all`,
      {},
      {
        withCredentials: true,
      }
    );
  }

  refreshToken(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/refresh`,
      {},
      {
        withCredentials: true,
      }
    );
  }

  getSessions(): Observable<SessionResponse[]> {
    return this.http.get<SessionResponse[]>(
      `${environment.apiUrl}/user/sessions`,
      {
        withCredentials: true,
      }
    );
  }

  deleteSession(sessionId: string): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(
      `${environment.apiUrl}/user/sessions/${sessionId}`,
      {
        withCredentials: true,
      }
    );
  }

  verifyEmail(token: string): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(
      `${this.apiUrl}/verify-email?token=${token}`
    );
  }

  resendVerification(
    request: EmailVerificationRequest
  ): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/resend-verification`,
      request
    );
  }

  forgotPassword(request: ForgotPasswordRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/forgot-password`,
      request
    );
  }

  resetPassword(request: ResetPassword): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/reset-password`,
      request
    );
  }
}
