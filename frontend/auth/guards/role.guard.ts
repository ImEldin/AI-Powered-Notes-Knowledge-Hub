import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthStateService } from '../service/auth-state.service';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(private authState: AuthStateService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot) {
    const requiredRoles = route.data['roles'] as string[];
    const userRole = this.authState.getUserRole();

    if (requiredRoles && requiredRoles.includes(userRole)) {
      return true;
    }

    return this.router.createUrlTree(['/dashboard']);
  }
}
