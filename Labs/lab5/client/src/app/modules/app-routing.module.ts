import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/auth/login/login.component';
import { RegisterComponent } from '../components/auth/register/register.component';
import { AuthGuard, LoggedGuard } from '../services/auth/auth.guard';
import { AuthComponent } from '../components/auth/auth.component';
import { LogoutComponent } from '../components/auth/logout.component';
import { AttendancesComponent } from '../components/attendances/attendances.component';

const routes: Routes = [
  // basic routes
  { path: '', redirectTo: 'attendaces', pathMatch: 'full' },
  { path: 'attendaces', component: AttendancesComponent, canActivate: [AuthGuard] },
  // authentication
  {
    path: 'login', component: AuthComponent, canActivate: [LoggedGuard],
    children: [{ path: '', component: LoginComponent }]
  },
  {
    path: 'register', component: AuthComponent, canActivate: [LoggedGuard],
    children: [{ path: '', component: RegisterComponent }]
  },
  {
    path: 'logout', component: LogoutComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
