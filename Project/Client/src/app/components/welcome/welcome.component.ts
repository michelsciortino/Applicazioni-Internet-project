import { Component, OnInit, OnDestroy } from '@angular/core';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css', '../common.css']
})
export class WelcomeComponent implements OnInit, OnDestroy {
    isMobile: Boolean;
    isMobileSub: Subscription;

    constructor(private isMobileSvc: IsMobileService) { }

    ngOnInit() {
        this.isMobileSub = this.isMobileSvc.isMobile.subscribe(
            (bool: Boolean) => { this.isMobile = bool; }
        )
    }

    ngOnDestroy() {
        this.isMobileSub.unsubscribe();
    }
}
