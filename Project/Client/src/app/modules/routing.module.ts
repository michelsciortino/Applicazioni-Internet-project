import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/authentication/login/login.component';
import { LoggedGuard, AuthGuard } from '../services/auth/auth.guard';
import { RecoveryComponent } from '../components/authentication/recovery/recovery.component';
import { LogoutComponent } from '../components/authentication/logout.component';
import { PasswordResetComponent } from '../components/authentication/password.reset/password-reset.component';
import { ConfirmComponent } from '../components/authentication/confirm/confirm.component';
import { RegisterComponent } from '../components/authentication/register/register.component';
import { MessageComponent } from '../components/message/message.component';

const routes: Routes = [
  // basic routes
  // { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  {
    path: 'auth/login', component: LoginComponent, canActivate: [LoggedGuard],
  },
  {
    path: 'auth/recovery',
    children: [
      {
        path: 'reset/:token',
        children: [
          {
            path: '', component: PasswordResetComponent, canActivate: [LoggedGuard],
          },
          {
            path: 'done', component: MessageComponent
          }
        ]
      },
      {
        path: '',
        children: [
          { path: '', component: RecoveryComponent, canActivate: [LoggedGuard] },
          { path: 'done', component: MessageComponent }
        ]
      }
    ]
  },
  {
    path: 'auth/confirm/:token',
    children: [
      { path: '', component: ConfirmComponent, canActivate: [LoggedGuard] },
      { path: 'done', component: MessageComponent }
    ]
  },
  {
    path: 'auth/register',
    children: [
      { path: '', component: RegisterComponent, canActivate: [LoggedGuard] },
      { path: 'done', component: MessageComponent }
    ]
  },
  {
    path: 'auth/logout', component: LogoutComponent, canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
