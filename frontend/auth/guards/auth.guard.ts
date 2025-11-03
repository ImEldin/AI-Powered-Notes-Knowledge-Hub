import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, timer } from 'rxjs';
import { map, filter, take, switchMap } from 'rxjs/operators';
import { AuthStateService } from '../service/auth-state.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authState: AuthStateService, private router: Router) {}

  canActivate(): Observable<boolean> {
    return this.authState.authInitialized$.pipe(
      filter((initialized) => initialized),
      take(1),
      map(() => {
        if (this.authState.isLoggedIn()) {
          return true;
        } else {
          this.router.navigate(['/auth/login']);
          return false;
        }
      })
    );
  }
}
