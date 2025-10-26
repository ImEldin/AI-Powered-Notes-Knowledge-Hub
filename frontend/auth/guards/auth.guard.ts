import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthStateService } from '../service/auth-state.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authState: AuthStateService, private router: Router) {}

  canActivate() {
    if (this.authState.isLoggedIn()) {
      return true;
    } else {
      return this.router.createUrlTree(['/auth/login']);
    }
  }
}
