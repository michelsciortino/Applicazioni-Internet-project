import { Component } from '@angular/core';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css', '../common.css']
})
export class WelcomeComponent {
    isMobile: Boolean;

    constructor(private isMobileSvc: IsMobileService) {
        this.isMobileSvc.isMobile.subscribe(
            (bool: Boolean) => { this.isMobile = bool; }
        )
    }
}
