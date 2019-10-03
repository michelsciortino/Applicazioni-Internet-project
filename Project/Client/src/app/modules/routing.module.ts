import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/authentication/login/login.component';
import { AuthGuard } from '../services/auth/guards/auth.guard';
import { RecoveryComponent } from '../components/authentication/recovery/recovery.component';
import { LogoutComponent } from '../components/authentication/logout.component';
import { PasswordResetComponent } from '../components/authentication/password-reset/password-reset.component';
import { ConfirmComponent } from '../components/authentication/confirm/confirm.component';
import { MessageComponent } from '../components/message/message.component';
import { HomeComponent } from '../components/home/home.component';
import { NotLoggedGuard } from '../services/auth/guards/not-logged.guard';
import { AdminComponent } from '../components/admin/admin.component';
import { CompanionComponent } from '../components/companion/companion.component';
import { WelcomeComponent } from '../components/welcome/welcome.component';
import { LinesComponent } from '../components/lines/lines.component';
import { AccountComponent } from '../components/account/account.component';
import { UserRole } from '../models/roles';
import { ParentComponent } from '../components/parent/parent.component';
import { RunningRaceComponent } from '../components/home/companion/running-race/running-race.component';

const routes: Routes = [
  // basic routes
  {
    path: '',
    component: WelcomeComponent,
    canActivate: [NotLoggedGuard]
  },
  {
    path: 'home', component: HomeComponent,
    canActivate: [AuthGuard],
  },
  { path: 'lines', component: LinesComponent },
  {
    path: 'account',
    component: AccountComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: [UserRole.ADMIN] }
  },
  {
    path: 'companion',
    component: CompanionComponent,
    canActivate: [AuthGuard],
    data: { roles: [UserRole.COMPANION] }
  },
  {
    path: 'runningRace/:lineName/:date/:direction', canActivate:[AuthGuard],
    component: RunningRaceComponent,
    data:{ roles: [UserRole.COMPANION]}
  },
  {
    path: 'parent',
    component: ParentComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'auth/login',
    component: LoginComponent,
    canActivate: [NotLoggedGuard],
  },
  {
    path: 'auth/recovery', canActivate: [NotLoggedGuard],
    children: [
      {
        path: 'reset/:token',
        children: [
          {
            path: '', component: PasswordResetComponent, canActivate: [NotLoggedGuard],
          },
          {
            path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard],
          }
        ]
      },
      {
        path: '', canActivate: [NotLoggedGuard],
        children: [
          { path: '', component: RecoveryComponent, canActivate: [NotLoggedGuard] },
          { path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard], }
        ]
      }
    ]
  },
  {
    path: 'auth/confirm/:token', canActivate: [NotLoggedGuard],
    children: [
      { path: '', component: ConfirmComponent, canActivate: [NotLoggedGuard] },
      { path: 'done', component: MessageComponent, canActivate: [NotLoggedGuard] }
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
