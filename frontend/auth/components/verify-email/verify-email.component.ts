import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { AuthStateService } from '../../service/auth-state.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { BrowserStorageService } from '../../../shared/services/browser-storage.service';

@Component({
  selector: 'app-verify-email',
  imports: [CommonModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css',
})
export class VerifyEmailComponent implements OnInit {
  email: string = '';
  resending: boolean = false;

  constructor(
    private authService: AuthService,
    private authState: AuthStateService,
    private router: Router,
    private notification: NotificationService,
    private storage: BrowserStorageService
  ) {}

  ngOnInit() {
    this.email = this.storage.getSessionItem('pendingVerificationEmail') || '';

    if (!this.email) {
      this.router.navigate(['/auth/login']);
    }
  }

  resendEmail() {
    this.resending = true;

    this.authService.resendVerification({ email: this.email }).subscribe({
      next: (response) => {
        this.notification.success(
          response.message ||
            'Verification email sent! Please check your inbox.'
        );
        this.resending = false;
      },
      error: (error) => {
        this.notification.error(
          error.error?.message || 'Failed to resend email'
        );
        this.resending = false;
      },
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.authState.clearAuthState();
        this.notification.success('Logged out successfully');
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        this.authState.clearAuthState();
        this.notification.error('Logged out locally');
        this.router.navigate(['/auth/login']);
      },
    });
  }
}
