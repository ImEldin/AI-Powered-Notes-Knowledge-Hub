import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../../auth/components/login/login.component';
import { DashboardComponent } from '../../features/dashboard/dashboard.component';
import { AuthGuard } from '../../auth/guards/auth.guard';
import { RegisterComponent } from '../../auth/components/register/register.component';
import { VerifyEmailComponent } from '../../auth/components/verify-email/verify-email.component';
import { EmailVerificationComponent } from '../../auth/components/email-verification/email-verification.component';

const routes: Routes = [
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
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
