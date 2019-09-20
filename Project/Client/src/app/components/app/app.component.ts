import { Component, ChangeDetectorRef, OnDestroy, ElementRef, ViewChild, OnInit, AfterViewInit, AfterContentInit } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material';
import { UserService } from 'src/app/services/user/user.service';
import { IsMobileService } from 'src/app/services/is-mobile/is-mobile.service';
import { UserInfo } from 'src/app/models/user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  private isLoggedSub: Subscription;
  private userInfoSub: Subscription;
  private isMobileSub: Subscription;

  isLogged: boolean;
  isAdmin: boolean;
  isCompanion: boolean;

  isMobile: boolean;

  @ViewChild('snav', { static: true })
  sidenav: MatSidenav;

  constructor(private router: Router, private authSvc: AuthService, private userSvc: UserService, private isMobileSvc: IsMobileService) { }

  ngOnInit() {
    this.isLoggedSub = this.authSvc.observeLoggedStatus().subscribe(
      (value: boolean) => this.isLogged = value);

    this.userInfoSub = this.userSvc.getUserInfo().subscribe(
      (info: UserInfo) => {
        if (info != null) {
          this.isAdmin = info.isAdmin();
          this.isCompanion = info.isCompanion();
        }
      }
    );

    this.isMobileSub = this.isMobileSvc.getIsMobile()
      .subscribe((isMobile) => {
        this.isMobile = isMobile;
        if (isMobile) {
          console.log("closing sidenav")
          this.sidenav.close();
        }
        else this.sidenav.open();
      })

    this.router.events.subscribe(event => {
      // close sidenav on routing
      if (this.isMobile)
        this.sidenav.close();
    });
  }

  ngOnDestroy(): void {
    this.isLoggedSub.unsubscribe();
    this.userInfoSub.unsubscribe();
  }
}
