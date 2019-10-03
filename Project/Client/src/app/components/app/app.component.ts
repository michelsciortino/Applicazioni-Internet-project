import { Component, ChangeDetectorRef, OnDestroy, ElementRef, ViewChild, OnInit, AfterViewInit, AfterContentInit } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatSidenav, MatIconRegistry } from '@angular/material';
import { UserService } from 'src/app/services/user/user.service';
import { IsMobileService } from 'src/app/services/is-mobile/is-mobile.service';
import { UserInfo } from 'src/app/models/user';
import { DomSanitizer } from '@angular/platform-browser';
import { NotificationService } from 'src/app/services/notifications/notification.service';
import { Notification } from 'src/app/models/notification';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  private isLoggedSub: Subscription;
  private userInfoSub: Subscription;
  private isMobileSub: Subscription;
  private unreadCountSub: Subscription;

  loggedUser: UserInfo;

  hasChildren: boolean;
  isLogged: boolean;
  isAdmin: boolean;
  isCompanion: boolean;
  unreadCount: number;
  notifications: Notification[]
  isMobile: boolean;

  @ViewChild('snav', { static: true })
  sidenav: MatSidenav;

  constructor(private router: Router, private authSvc: AuthService, private notificationSvc: NotificationService, private userSvc: UserService, private isMobileSvc: IsMobileService, private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer) {
    /*this.matIconRegistry.addSvgIcon(
      "imageUser",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../assets/userColor.svg")
    );*/
    this.unreadCount = 0;
  }

  ngOnInit() {
    this.isLoggedSub = this.authSvc.observeLoggedStatus().subscribe(
      (value: boolean) => this.isLogged = value);

    this.userInfoSub = this.userSvc.getUserInfo().subscribe(
      (user: UserInfo) => {
        if (user != null) {
          this.loggedUser = user;
          this.isAdmin = user.isAdmin();
          this.isCompanion = user.isCompanion();
          this.hasChildren = (user.children != null && user.children.length > 0);
        }
      }
    );

    this.unreadCountSub = this.notificationSvc.getUnreadCount()
      .subscribe(count => this.unreadCount = count);

    this.notificationSvc.listen(() => { this.getNotifications() });

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
    this.isMobileSub.unsubscribe();
    this.unreadCountSub.unsubscribe();
  }

  getNotifications() {
    this.notifications = this.notificationSvc.getNotifications()
  }

  openNotification(notification) {
    this.notificationSvc.readNotification(notification)
  }
}
