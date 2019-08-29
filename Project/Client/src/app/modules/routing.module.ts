import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/authentication/login/login.component';
import { AuthenticatedGuard } from '../services/auth/guards/auth.guard';
import { RecoveryComponent } from '../components/authentication/recovery/recovery.component';
import { LogoutComponent } from '../components/authentication/logout.component';
import { PasswordResetComponent } from '../components/authentication/password.reset/password-reset.component';
import { ConfirmComponent } from '../components/authentication/confirm/confirm.component';
import { RegisterComponent } from '../components/authentication/register/register.component';
import { MessageComponent } from '../components/message/message.component';
import { HomeComponent } from '../components/home/home.component';
import { NotLoggedGuard } from '../services/auth/guards/non-logged.guard';
import { AdminGuard } from '../services/auth/guards/admin.guard';
import { AdminComponent } from '../components/admin/admin.component';
import { CompanionGuard } from '../services/auth/guards/companion.guard';
import { CompanionComponent } from '../components/companion/companion.component';
import { WelcomeComponent } from '../components/welcome/welcome.component';

const routes: Routes = [
  // basic routes
  { path: '', component: WelcomeComponent, canActivate: [NotLoggedGuard] },
  { path: 'home', component: HomeComponent, canActivate: [AuthenticatedGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthenticatedGuard, AdminGuard] },
  { path: 'companion', component: CompanionComponent, canActivate: [AuthenticatedGuard, CompanionGuard] },
  // can be activated only if the user is not logged
  {
    path: 'auth/login', component: LoginComponent, canActivate: [NotLoggedGuard],
  },
  // can be activated only if the user is not logged
  {
    path: 'auth/recovery', canActivate: [NotLoggedGuard],
    children: [
      {
        path: 'reset/:token',
        children: [
          // can be activated only if the user is not logged
          {
            path: '', component: PasswordResetComponent, canActivate: [NotLoggedGuard],
          },
          // can be activated only if the user is not logged
          {
            path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard],
          }
        ]
      },
      // can be activated only if the user is not logged
      {
        path: '', canActivate: [NotLoggedGuard],
        children: [
          // can be activated only if the user is not logged
          { path: '', component: RecoveryComponent, canActivate: [NotLoggedGuard] },
          // can be activated only if the user is not logged
          { path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard], }
        ]
      }
    ]
  },
  // can be activated only if the user is not logged
  {
    path: 'auth/confirm/:token', canActivate: [NotLoggedGuard],
    children: [
      // can be activated only if the user is not logged
      { path: '', component: ConfirmComponent, canActivate: [NotLoggedGuard] },
      // can be activated only if the user is not logged
      { path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard] }
    ]
  },
  // can be activated only if the user is logged and is an admin
  {
    path: 'auth/register',
    children: [
      { path: '', component: RegisterComponent, canActivate: [AuthenticatedGuard, AdminGuard] },
      { path: 'done', component: MessageComponent }
    ]
  },
  // can be activated only if the user is logged
  {
    path: 'auth/logout', component: LogoutComponent, canActivate: [AuthenticatedGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
