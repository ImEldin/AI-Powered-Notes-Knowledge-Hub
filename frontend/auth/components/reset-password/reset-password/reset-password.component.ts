import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { NotificationService } from '../../../../shared/services/notification.service';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  loading = false;
  token: string = '';
  hidePassword = true;
  hideConfirmPassword = true;
  resetSuccess = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: NotificationService
  ) {
    this.resetPasswordForm = this.fb.group(
      {
        newPassword: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.maxLength(64),
            Validators.pattern(
              /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/
            ),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.token = params['token'];
      if (!this.token) {
        this.notification.error('Invalid reset link');
        this.router.navigate(['/auth/forgot-password']);
      }
    });
  }

  passwordMatchValidator(control: AbstractControl) {
    const password = control.get('newPassword');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    return password.value === confirmPassword.value
      ? null
      : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.resetPasswordForm.invalid) {
      this.resetPasswordForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.authService
      .resetPassword({
        token: this.token,
        newPassword: this.resetPasswordForm.value.newPassword,
      })
      .subscribe({
        next: () => {
          this.resetSuccess = true;
          this.notification.success('Password reset successful!');
          setTimeout(() => {
            this.router.navigate(['/auth/login']);
          }, 3000);
        },
        error: () => {
          this.notification.error('Failed to reset password');
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  getPasswordError() {
    const control = this.resetPasswordForm.get('newPassword');
    if (control?.hasError('required')) return 'Password is required';
    if (control?.hasError('minlength'))
      return 'Password must be at least 8 characters';
    if (control?.hasError('maxlength'))
      return 'Password must not exceed 64 characters';
    if (control?.hasError('pattern'))
      return 'Password must contain uppercase, lowercase, number, and special character';
    return '';
  }

  getConfirmPasswordError() {
    const control = this.resetPasswordForm.get('confirmPassword');
    if (control?.hasError('required')) return 'Please confirm your password';
    if (this.resetPasswordForm.hasError('passwordMismatch') && control?.touched)
      return 'Passwords do not match';
    return '';
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility() {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }
}
