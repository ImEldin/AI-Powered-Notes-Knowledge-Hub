import { Component, OnInit, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../service/auth.service';
import { AuthStateService } from '../../service/auth-state.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { BrowserStorageService } from '../../../shared/services/browser-storage.service';
import { environment } from '../../../src/environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatIconModule,
    RouterModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  authError: string | null = null;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private authState: AuthStateService,
    private router: Router,
    private notificationService: NotificationService,
    private storage: BrowserStorageService,
    private ngZone: NgZone
  ) {
    this.loginForm = this.fb.group({
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(255)],
      ],
      password: ['', [Validators.required, Validators.maxLength(64)]],
      rememberMe: [false],
    });
  }

  ngOnInit() {
    if (!this.storage.isBrowserPlatform()) return;
    google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => this.handleGoogleResponse(response),
    });

    google.accounts.id.renderButton(document.getElementById('google-btn'), {
      theme: 'outline',
      size: 'large',
      width: 375,
      text: 'signin_with',
      shape: 'rectangular',
      logo_alignment: 'left',
    });
  }

  handleGoogleResponse(response: any) {
    const idToken = response.credential;

    this.loading = true;
    this.ngZone.run(() => {
      this.authService.googleLogin({ idToken }).subscribe({
        next: () => {
          this.authService.getCurrentUser().subscribe({
            next: (user) => {
              this.authState.setAuthenticated(true);
              this.authState.setUserRole(user.role);
              this.authState.setAuthInitialized(true);
              this.storage.setItem(
                'emailVerified',
                user.emailVerified.toString()
              );

              this.notificationService.success('Login successful!');
              this.router.navigate(['/dashboard']);

              this.loading = false;
            },
            error: () => {
              this.loading = false;
              this.notificationService.error(
                'Failed to load user data. Please try logging in again.'
              );
              this.authState.clearAuthState();
              this.authState.setAuthInitialized(true);
            },
          });
        },
        error: () => {
          this.loading = false;
          this.authError = 'Google login failed.';
          this.notificationService.error('Google login failed.');
        },
      });
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.clearAuthError();

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        setTimeout(() => {
          this.authService.getCurrentUser().subscribe({
            next: (user) => {
              this.authState.setAuthenticated(true);
              this.authState.setUserRole(user.role);
              this.storage.setItem(
                'emailVerified',
                user.emailVerified.toString()
              );

              if (!user.emailVerified) {
                this.notificationService.info(
                  'Please verify your email to continue'
                );
                this.storage.setSessionItem(
                  'pendingVerificationEmail',
                  this.loginForm.value.email
                );
                this.router.navigate(['/auth/verify-email']);
              } else {
                this.notificationService.success('Login successful!');
                this.router.navigate(['/dashboard']);
              }

              this.loading = false;
            },
            error: () => {
              this.loading = false;
              this.authError = 'Login failed. Please try again.';
              this.notificationService.error(
                'Failed to load user data. Please try logging in again.'
              );
              this.authState.clearAuthState();
              this.authState.setAuthInitialized(true);
            },
          });
        }, 100);
      },
      error: () => {
        this.authError = 'Invalid email or password.';
        this.notificationService.error('Login failed');
        this.loading = false;
      },
    });
  }

  getEmailError() {
    const emailControl = this.loginForm.get('email');
    if (emailControl?.hasError('required')) {
      return 'Email is required';
    }
    if (emailControl?.hasError('email')) {
      return 'Email should be valid';
    }
    if (emailControl?.hasError('maxlength')) {
      return 'Email must not exceed 255 characters';
    }
    return '';
  }

  getPasswordError() {
    const passwordControl = this.loginForm.get('password');
    if (passwordControl?.hasError('required')) {
      return 'Password is required';
    }
    if (passwordControl?.hasError('maxlength')) {
      return 'Password must not exceed 64 characters';
    }
    return '';
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  clearAuthError() {
    this.authError = null;
  }
}
