import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { AuthStateService } from '../../service/auth-state.service';
import { NotificationService } from '../../../shared/services/notification.service';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private authState: AuthStateService,
    private router: Router,
    private notification: NotificationService
  ) {
    this.registerForm = this.fb.group(
      {
        email: [
          '',
          [Validators.required, Validators.email, Validators.maxLength(255)],
        ],
        firstName: ['', [Validators.required, Validators.maxLength(50)]],
        lastName: ['', [Validators.required, Validators.maxLength(50)]],
        password: [
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

  passwordMatchValidator(control: AbstractControl) {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    return password.value === confirmPassword.value
      ? null
      : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    const { confirmPassword, ...registerData } = this.registerForm.value;

    this.authService.register(registerData).subscribe({
      next: (response) => {
        this.notification.success(
          'Registration successful! Please verify your email.'
        );

        sessionStorage.setItem('pendingVerificationEmail', registerData.email);

        if (response.emailVerified) {
          this.authState.setAuthenticated(true);
          this.router.navigate(['/dashboard']);
        } else {
          this.authState.setAuthenticated(true);
          this.router.navigate(['/auth/verify-email']);
        }
      },
      error: (error) => {
        this.notification.error(
          error.error?.message || 'Registration failed. Please try again.'
        );
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  getFirstNameError() {
    const control = this.registerForm.get('firstName');
    if (control?.hasError('required')) return 'First name is required';
    if (control?.hasError('maxlength'))
      return 'First name must not exceed 50 characters';
    return '';
  }

  getLastNameError() {
    const control = this.registerForm.get('lastName');
    if (control?.hasError('required')) return 'Last name is required';
    if (control?.hasError('maxlength'))
      return 'Last name must not exceed 50 characters';
    return '';
  }

  getEmailError() {
    const control = this.registerForm.get('email');
    if (control?.hasError('required')) return 'Email is required';
    if (control?.hasError('email')) return 'Email should be valid';
    if (control?.hasError('maxlength'))
      return 'Email must not exceed 255 characters';
    return '';
  }

  getPasswordError() {
    const control = this.registerForm.get('password');
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
    const control = this.registerForm.get('confirmPassword');
    if (control?.hasError('required')) return 'Please confirm your password';
    if (this.registerForm.hasError('passwordMismatch') && control?.touched) {
      return 'Passwords do not match';
    }
    return '';
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility() {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }
}
