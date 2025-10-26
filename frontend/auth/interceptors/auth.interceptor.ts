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
        notificationService.error('Session expired. Please login again.');
        authState.setAuthenticated(false);
        router.navigate(['/auth/login']);
      }
      return throwError(() => error);
    })
  );
};
