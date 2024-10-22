import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css', '../common.css']
})
export class HomeComponent implements OnInit, OnDestroy {
    isCompanion: boolean;
    hasChildren: boolean;

    userSub: Subscription;

    constructor(private authSvc: AuthService, private userSvc: UserService, private router: Router) {
    }

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.isCompanion = info.isCompanion();
                    this.hasChildren = info.children != null && info.children.length > 0;
                }
            }
        )
    }

    ngOnDestroy() {
        this.userSub.unsubscribe();
    }
}
