import { Component, NgZone } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-logout',
    template: 'logging out...'
})
export class LogoutComponent {
    constructor(private ngZone: NgZone,authSvc: AuthService, private router: Router) {
        authSvc.logout();
        this.ngZone.run(() => this.router.navigate(['/'])).then();
    }
}
