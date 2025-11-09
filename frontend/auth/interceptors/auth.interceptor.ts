import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthStateService } from '../service/auth-state.service';
import { NotificationService } from '../../shared/services/notification.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authState = inject(AuthStateService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        const wasAuthenticated = authState.isLoggedIn();

        authState.clearAuthState();

        if (wasAuthenticated) {
          notificationService.error(
            error.error?.message || 'Session expired. Please login again.'
          );
          router.navigate(['/auth/login']);
        } else {
          const message = error.error?.message || 'Invalid email or password.';
          notificationService.error(message);
        }
      }

      if (error.status === 403) {
        notificationService.error(
          error.error?.message ||
            'You do not have permission to access this resource.'
        );
      }

      if (error.status === 409) {
        const message =
          error.error?.message || 'A conflict occurred. Please try again.';
        notificationService.error(message);
      }

      if (error.status >= 500) {
        error.error?.message ||
          notificationService.error('Server error. Please try again later.');
      }

      return throwError(() => error);
    })
  );
};
