import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { UserService } from '../../user/user.service';
import { UserInfo } from '../../user/models/user';
import { UserRole } from '../../user/models/roles';
import { Observable, isObservable } from 'rxjs';
import { map } from 'rxjs/operators';
import { isBoolean } from 'util';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
    constructor(private userSvc: UserService, private router: Router) {
    }

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | boolean {
        const userinfo = this.userSvc.getUserInfo();
        if (isObservable(userinfo))
            return userinfo.pipe(
                map(
                    (info: UserInfo) => {
                        console.log("INFO:", JSON.stringify(info));
                        if (info == null) return false;
                        return info.isAdmin();
                    }
                )
            );
        else {
            if (userinfo == null || userinfo.isAdmin() == false) return false;
            return true;
        }
    }
}
