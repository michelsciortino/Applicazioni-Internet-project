import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from '../components/auth/login/login.component'
import { RegisterComponent } from '../components/auth/register/register.component';
import { AuthGuard } from '../services/auth/auth.guard';
import { HomeComponent } from '../components/home/home.component';
import { AuthComponent } from '../components/auth/auth.component';
import { LogoutComponent } from '../components/auth/logout.component';
const routes: Routes = [

  // basic routes
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  // authentication
  {
    path: 'login', component: AuthComponent,
    children: [{ path: '', component: LoginComponent }]
  },
  {
    path: 'register', component: AuthComponent,
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
export class AppRoutingModule { }