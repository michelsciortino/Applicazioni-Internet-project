import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserInfo } from 'src/app/models/user';
import { UserService } from 'src/app/services/user/user.service';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material';

@Component({
    selector: 'app-contacts-setting',
    templateUrl: './contacts-setting.component.html',
    styleUrls: ['./contacts-setting.component.css']
})
export class ContactsSettingComponent implements OnInit, OnDestroy {
    userInfo: UserInfo;
    contacts: String[];
    contact: string;
    localInfo: UserInfo = new UserInfo;

    private userInfoSub: Subscription;

    constructor(private userSvc: UserService, public dialog: MatDialog) {
    }

    ngOnInit() {
        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    //console.log(info);
                    this.userInfo = info;
                    this.contacts = info.contacts;
                    this.localInfo = JSON.parse(JSON.stringify(info));
                }
            }
        );
    }

    ngOnDestroy(): void {
        this.userInfoSub.unsubscribe();
    }

    //TODO: Endpoint not present
    addContact(): void {
        //console.log("ADD CONTACT", this.contact)
        this.localInfo.contacts.push(this.contact);
        this.userSvc.updateUser(this.localInfo);
    }

    removeContact(contact: string): void {
        //console.log("REMOVE CONTACT:", contact)
        this.localInfo.contacts.splice(this.localInfo.contacts.indexOf(contact), 1);
        this.userSvc.updateUser(this.localInfo);
    }

    isComplete(): boolean {
        return this.contact != null && this.contact.length > 3;
    }
}
