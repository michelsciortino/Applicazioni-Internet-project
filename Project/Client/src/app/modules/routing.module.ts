import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/authentication/login/login.component';
import { LoggedGuard, AuthGuard } from '../services/auth/auth.guard';
import { RecoveryComponent } from '../components/authentication/recovery/recovery.component';
import { LogoutComponent } from '../components/authentication/logout.component';
import { PasswordResetComponent } from '../components/authentication/password.reset/password-reset.component';

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
        path: 'reset', component: PasswordResetComponent, canActivate: [LoggedGuard]
      },
      {
        path: '', component: RecoveryComponent, canActivate: [LoggedGuard]
      }
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
