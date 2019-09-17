import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserInfo } from 'src/app/models/user';
import { UserService } from 'src/app/services/user/user.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-my-account',
    templateUrl: './my-account.component.html',
    styleUrls: ['./my-account.component.css']
})
export class MyAccountComponent implements OnInit, OnDestroy {
    myInfo: UserInfo = new UserInfo;
    private userInfoSub: Subscription;

    constructor(private userSvc: UserService) {
    }

    ngOnInit() {
        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    //console.log(info);
                    this.myInfo = info;
                }
            }
        );
    }

    ngOnDestroy(): void {
        this.userInfoSub.unsubscribe();
    }
}
