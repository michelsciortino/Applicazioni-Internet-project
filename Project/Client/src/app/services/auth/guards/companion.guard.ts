import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { UserService } from '../../user/user.service';
import { UserInfo } from '../../user/models/user';
import { UserRole } from '../../user/models/roles';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class CompanionGuard implements CanActivate {
    constructor(private userSvc: UserService, private router: Router) {
    }

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | boolean {
        return this.userSvc.getUserInfo().pipe(
            map(
                (info: UserInfo) => {
                    if (!info.roles.find(r => r === UserRole.COMPANION)) {
                        console.log("info.roles does not cotain COMPANION role");
                        this.router.navigate(['/']);
                        return false;
                    } else {
                        console.log("info.roles cotains COMPANION role");
                        return true;
                    }
                }
            )
        );
    }
}
