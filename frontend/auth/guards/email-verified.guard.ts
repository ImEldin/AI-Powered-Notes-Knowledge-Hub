import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { BrowserStorageService } from '../../shared/services/browser-storage.service';

export const emailVerifiedGuard: CanActivateFn = () => {
  const router = inject(Router);
  const storage = inject(BrowserStorageService);

  const emailVerified = storage.getItem('emailVerified') === 'true';

  if (emailVerified) {
    return true;
  } else {
    return router.createUrlTree(['/auth/verify-email']);
  }
};
