import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { AuthStateService } from '../../service/auth-state.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { BrowserStorageService } from '../../../shared/services/browser-storage.service';

@Component({
  selector: 'app-email-verification',
  imports: [CommonModule],
  templateUrl: './email-verification.component.html',
  styleUrl: './email-verification.component.css',
})
export class EmailVerificationComponent implements OnInit, OnDestroy {
  verifying: boolean = true;
  success: boolean = false;
  message: string = 'Verifying your email...';
  private hasVerified = false;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private authState: AuthStateService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef,
    private storage: BrowserStorageService
  ) {}

  ngOnInit() {
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe((params) => {
        const token = params['token'];

        if (token && !this.hasVerified) {
          this.verifyEmail(token);
        } else if (!token) {
          this.verifying = false;
          this.success = false;
          this.message = 'Invalid verification link';
          this.notification.error('Invalid verification link');
          this.cdr.detectChanges();
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  verifyEmail(token: string) {
    if (this.hasVerified) {
      return;
    }
    this.hasVerified = true;

    this.authService
      .verifyEmail(token)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.verifying = false;
          this.success = true;
          this.message = response.message || 'Email verified successfully!';

          this.cdr.detectChanges();

          this.notification.success(
            'Email verified! Redirecting to dashboard...'
          );

          this.storage.removeSessionItem('pendingVerificationEmail');

          this.authService.getCurrentUser().subscribe({
            next: (user) => {
              this.authState.setAuthenticated(true);
              this.authState.setUserRole(user.role);
              this.storage.setItem(
                'emailVerified',
                user.emailVerified.toString()
              );

              setTimeout(() => {
                this.router.navigate(['/dashboard']);
              }, 2000);
            },
            error: () => {
              this.notification.info('Please log in to continue');
              setTimeout(() => {
                this.router.navigate(['/auth/login']);
              }, 2000);
            },
          });
        },
        error: (error) => {
          this.verifying = false;
          this.success = false;
          this.message = error.error?.message || 'Email verification failed';

          this.cdr.detectChanges();

          this.notification.error(this.message);
        },
      });
  }

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }
}
