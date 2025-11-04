import { Component, inject, OnInit, Renderer2 } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/service/auth.service';
import { AuthStateService } from '../../auth/service/auth-state.service';
import { filter } from 'rxjs/operators';
import { BrowserStorageService } from '../../shared/services/browser-storage.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'frontend';
  private renderer = inject(Renderer2);
  private router = inject(Router);
  private firstNavigation = true;

  constructor(
    private authService: AuthService,
    private authState: AuthStateService,
    private storage: BrowserStorageService
  ) {}

  ngOnInit() {
    if (this.storage.isBrowserPlatform()) {
      this.router.events
        .pipe(filter((event) => event instanceof NavigationEnd))
        .subscribe(() => {
          const delay = this.firstNavigation ? 100 : 0;
          this.firstNavigation = false;

          setTimeout(() => {
            const routedComponent = document.querySelector('router-outlet + *');
            if (routedComponent) {
              this.renderer.addClass(routedComponent, 'navigation-complete');
            }
          }, delay);
        });

      this.checkAuthStatus();
    } else {
      this.authState.setAuthInitialized(true);
    }
  }

  private checkAuthStatus() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.authState.setAuthenticated(true);
        this.authState.setUserRole(user.role);
        this.storage.setItem('emailVerified', user.emailVerified.toString());

        setTimeout(() => {
          this.authState.setAuthInitialized(true);
        }, 0);
      },
      error: () => {
        this.authState.clearAuthState();

        setTimeout(() => {
          this.authState.setAuthInitialized(true);
        }, 0);
      },
    });
  }
}
