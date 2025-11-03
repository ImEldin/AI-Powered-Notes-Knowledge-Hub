import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/service/auth.service';
import { AuthStateService } from '../../auth/service/auth-state.service';
import { NotificationService } from '../../shared/services/notification.service';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  constructor(
    private authService: AuthService,
    private authState: AuthStateService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.authState.setAuthenticated(false);
        this.router.navigate(['/auth/login']);
      },
      error: (error) => {
        this.notificationService.error('Logout failed: ' + error.message);
      },
    });
  }
}
