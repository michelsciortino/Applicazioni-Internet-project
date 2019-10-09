import { Injectable, Output, EventEmitter, ChangeDetectorRef, OnDestroy, ApplicationRef } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MediaMatcher } from '@angular/cdk/layout';

@Injectable()
export class IsMobileService implements OnDestroy {
    private isMobileSbj: BehaviorSubject<boolean>;

    mobileQuery: MediaQueryList;
    private mobileQueryListener: () => void;

    constructor(applicationRef: ApplicationRef, media: MediaMatcher) {
        this.mobileQuery = media.matchMedia('(max-width: 690px)');
        this.isMobileSbj = new BehaviorSubject(this.mobileQuery.matches);
        this.mobileQueryListener = () => {
            this.isMobileSbj.next(this.mobileQuery.matches)
        };
        this.mobileQuery.addListener(this.mobileQueryListener);
    }

    public getIsMobile(): Observable<boolean> {
        return this.isMobileSbj.asObservable();
    }

    ngOnDestroy() {
        this.mobileQuery.removeListener(this.mobileQueryListener);
    }
}