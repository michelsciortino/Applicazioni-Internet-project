import { Component, OnInit, OnDestroy } from '@angular/core';
import { IsMobileService } from 'src/app/services/is-mobile/is-mobile.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css', '../common.css']
})
export class WelcomeComponent implements OnInit, OnDestroy {
    isMobile: boolean;
    isMobileSub: Subscription;

    constructor(private isMobileSvc: IsMobileService) { }

    ngOnInit() {
        this.isMobileSub = this.isMobileSvc.getIsMobile()
            .subscribe((isMobile) => this.isMobile = isMobile);
    }

    ngOnDestroy() {
        this.isMobileSub.unsubscribe();
    }
}
