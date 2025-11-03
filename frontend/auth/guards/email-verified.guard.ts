import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const emailVerifiedGuard: CanActivateFn = () => {
  const router = inject(Router);

  const emailVerified = localStorage.getItem('emailVerified') === 'true';

  if (emailVerified) {
    return true;
  } else {
    return router.createUrlTree(['/auth/verify-email']);
  }
};
