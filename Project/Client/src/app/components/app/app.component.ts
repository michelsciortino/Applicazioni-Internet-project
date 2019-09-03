import { Component, ChangeDetectorRef, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Observable, Subscription, isObservable } from 'rxjs';
import { take, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/services/user/models/user';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  mobileQuery: MediaQueryList;
  private mobileQueryListener: () => void;
  private isLoggedSub: Subscription;
  private userInfoSub: Subscription;
  isLogged: boolean;
  isAdmin: boolean;
  isCompanion: boolean;

  @ViewChild('snav', { static: true })
  sidenav: MatSidenav;

  constructor(private router: Router, private authSvc: AuthService, private userSvc: UserService, private isMobileSvc: IsMobileService, changeDetectorRef: ChangeDetectorRef, media: MediaMatcher) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this.mobileQueryListener = () => {
      if (this.mobileQuery.matches) {
        this.sidenav.close();
        this.isMobileSvc.setIsMobile(true);
      }
      else{
        this.sidenav.open();
        this.isMobileSvc.setIsMobile(false);
      }
      changeDetectorRef.detectChanges();
    };

    // tslint:disable-next-line: deprecation
    this.mobileQuery.addListener(this.mobileQueryListener);

    this.isLoggedSub = this.authSvc.observeLoggedStatus().subscribe(
      (value: boolean) => this.isLogged = value);

    this.userInfoSub = this.userSvc.observeUserInfo().subscribe(
      (info: UserInfo) => {
        if (info != null) {
          this.isAdmin = info.isAdmin();
          this.isCompanion = info.isCompanion();
        }
      }
    );

    this.router.events.subscribe(event => {
      // close sidenav on routing
      if (this.mobileQuery.matches)
        this.sidenav.close();
    });
  }

  ngOnDestroy(): void {
    // tslint:disable-next-line: deprecation
    this.mobileQuery.removeListener(this.mobileQueryListener);
    this.isLoggedSub.unsubscribe();
  }
}
