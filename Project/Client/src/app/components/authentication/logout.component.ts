import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';

@Component({
    selector: 'app-logout',
    template: 'logging out...'
})
export class LogoutComponent {
    constructor(authSvc: AuthService) {
        authSvc.logout();
    }
}
