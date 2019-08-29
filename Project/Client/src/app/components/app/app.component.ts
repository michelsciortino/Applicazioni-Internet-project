import { Component, ChangeDetectorRef, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { MediaMatcher } from '@angular/cdk/layout';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  mobileQuery: MediaQueryList;
  private mobileQueryListener: () => void;
  private loggedStatusSub: Subscription;
  loggedStatus: boolean;

  @ViewChild('snav', { static: true })
  sidenav: MatSidenav;

  constructor(private router: Router, private authSvc: AuthService, changeDetectorRef: ChangeDetectorRef, media: MediaMatcher) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this.mobileQueryListener = () => {
      if (this.mobileQuery.matches)
        this.sidenav.close();
      else
        this.sidenav.open();
      changeDetectorRef.detectChanges();
    };

    // tslint:disable-next-line: deprecation
    this.mobileQuery.addListener(this.mobileQueryListener);

    this.loggedStatusSub = this.authSvc.observeLoggedStatus().subscribe(
      (value: boolean) => this.loggedStatus = value
    );

    this.router.events.subscribe(event => {
      console.log(this.sidenav);
      // close sidenav on routing
      if (this.mobileQuery.matches)
        this.sidenav.close();
    });
  }

  ngOnDestroy(): void {
    // tslint:disable-next-line: deprecation
    this.mobileQuery.removeListener(this.mobileQueryListener);
  }

  public LoggedStatus(): Observable<boolean> {
    return this.authSvc.observeLoggedStatus();
  }
}
