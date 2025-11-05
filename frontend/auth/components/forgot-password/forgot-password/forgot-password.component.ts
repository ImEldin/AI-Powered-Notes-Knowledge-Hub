import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { NotificationService } from '../../../../shared/services/notification.service';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  loading = false;
  emailSent = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.forgotPasswordForm = this.fb.group({
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(255)],
      ],
    });
  }

  onSubmit() {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.authService
      .forgotPassword({ email: this.forgotPasswordForm.value.email })
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.emailSent = true;
          this.notificationService.success('Password reset email sent');
        },
        error: (error) => {
          this.loading = false;
          this.notificationService.error('Failed to send password reset email');
        },
      });
  }

  getEmailError() {
    const emailControl = this.forgotPasswordForm.get('email');
    if (emailControl?.hasError('required')) {
      return 'Email is required';
    }
    if (emailControl?.hasError('email')) {
      return 'Email should be valid';
    }
    if (emailControl?.hasError('maxLength')) {
      return 'Email should not exceed 255 characters';
    }
    return '';
  }

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }
}
