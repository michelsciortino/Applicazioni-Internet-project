import { Component, ChangeDetectorRef, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material';
import { UserService } from 'src/app/services/user/user.service';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';
import { UserInfo } from 'src/app/models/user';

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
    this.mobileQuery = media.matchMedia('(max-width: 690px)');
    this.mobileQueryListener = () => {
      if (this.mobileQuery.matches) {
        this.sidenav.close();
        this.isMobileSvc.setIsMobile(true);
      }
      else {
        this.sidenav.open();
        this.isMobileSvc.setIsMobile(false);
      }
      changeDetectorRef.detectChanges();
    };

    // tslint:disable-next-line: deprecation
    this.mobileQuery.addListener(this.mobileQueryListener);

    this.isLoggedSub = this.authSvc.observeLoggedStatus().subscribe(
      (value: boolean) => this.isLogged = value);

    this.userInfoSub = this.userSvc.getUserInfo().subscribe(
      (info: UserInfo) => {
        if (info != null) {
          this.isAdmin = UserInfo.prototype.isAdmin(info);
          this.isCompanion = UserInfo.prototype.isCompanion(info);
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
    this.userInfoSub.unsubscribe();
  }
}
