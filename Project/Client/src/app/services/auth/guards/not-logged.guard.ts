import { Injectable, NgZone } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class NotLoggedGuard implements CanActivate {
    constructor(private ngZone: NgZone,private authService: AuthService, private router: Router) {
    }

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean {
        if (this.authService.isLoggedIn()) {
            this.ngZone.run(() => this.router.navigate(['/home'])).then();
            return false;
        }
        return true;
    }
}
