import { Routes } from '@angular/router';
import { LoginComponent } from '../../auth/components/login/login.component';
import { DashboardComponent } from '../../features/dashboard/dashboard.component';
import { AuthGuard } from '../../auth/guards/auth.guard';
import { RegisterComponent } from '../../auth/components/register/register.component';
import { VerifyEmailComponent } from '../../auth/components/verify-email/verify-email.component';
import { EmailVerificationComponent } from '../../auth/components/email-verification/email-verification.component';
import { emailVerifiedGuard } from '../../auth/guards/email-verified.guard';
import { RoleGuard } from '../../auth/guards/role.guard';
import { ForgotPasswordComponent } from '../../auth/components/forgot-password/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from '../../auth/components/reset-password/reset-password/reset-password.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/auth/login',
    pathMatch: 'full',
  },
  {
    path: 'auth/login',
    component: LoginComponent,
  },
  {
    path: 'auth/register',
    component: RegisterComponent,
  },
  {
    path: 'auth/forgot-password',
    component: ForgotPasswordComponent,
  },
  { path: 'auth/reset-password', component: ResetPasswordComponent },
  {
    path: 'auth/email-verification',
    component: EmailVerificationComponent,
  },
  {
    path: 'auth/verify-email',
    component: VerifyEmailComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard, emailVerifiedGuard, RoleGuard],
    data: { roles: ['USER', 'ADMIN'] },
  },
];
