import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      console.log("GUARD", route.data.roles)
      console.log(currentUser.roles)
      if (!route.data.role) return this.authService.isLoggedIn();
      for (let role of route.data.roles) {
        for (let urole of currentUser.roles)
          if (urole === role) return true;
      }
      /*if (this.authService.isLoggedIn())
        return true;*/
        console.log("GUARD NO ROLE MATCH")
      this.router.navigate(['auth/login']);
      return false;
    }
    this.router.navigate(['auth/login']);
    return false;
  }
}
