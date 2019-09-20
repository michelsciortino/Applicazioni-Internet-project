import { Component } from "@angular/core";
import { CompanionRequest } from 'src/app/models/companion-request';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';
import { CompanionRequestsDataSource, AdminService } from 'src/app/services/admin/admin.service';
import { EditRaceDialog } from '../edit-race/edit-race.dialog';


enum RequestsType {
    PENDING,
    ACCEPTED,
    CONFIRMED
}

@Component({
    selector: 'app-companion-requests',
    templateUrl: './companion-requests.component.html',
    styleUrls: ['companion-requests.component.css']
})
export class CompanionRequestsManagementComponent {
    RequestsTab = RequestsType;
    
    activeTab: RequestsType;
    requests: CompanionRequest[];
    datasource: CompanionRequestsDataSource;

    requestsSub: Subscription;

    columnDefinitions = ["line", "direction", "date", "initialStop", "finalStop"];


    constructor(private adminSvc: AdminService, private dialog: MatDialog) {
        this.requests = [];
        this.activeTab = RequestsType.PENDING;
        this.datasource = new CompanionRequestsDataSource(this.adminSvc);
        this.requestsSub = this.datasource.getPendingRequests()
            .subscribe((requests) => {
                this.requests = requests;
                console.log(requests);
            });
    }

    switchToTab(tab: RequestsType) {
        this.activeTab = tab;
        switch (tab) {
            case RequestsType.PENDING:
                this.requestsSub.unsubscribe();
                this.requestsSub = this.datasource.getPendingRequests()
                    .subscribe((requests) => this.requests = requests);
                break;
            case RequestsType.ACCEPTED:
                this.requestsSub.unsubscribe();
                this.requestsSub = this.datasource.getAcceptedRequests()
                    .subscribe((requests) => this.requests = requests);
                break;
            case RequestsType.CONFIRMED:
                this.requestsSub.unsubscribe();
                this.requestsSub = this.datasource.getConfirmedRequests()
                    .subscribe((requests) => this.requests = requests);
                break;
        }
    }

    acceptRequest(request: CompanionRequest) {
        this.dialog.open(ConfirmDialog, { data: { title: "Accept request", message: "Are you sure to acept this request?", YES: true, CANCEL: true } })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        this.adminSvc.acceptCompanionRequest(request.lineName, request.direction, request.date, request.username).toPromise()
                            .then(response => this.datasource.reload())
                            .catch(error => console.log(error));
                        break;
                    default:
                        break;
                }
            })
    };

    rejectRequest(request: CompanionRequest) {
        this.dialog.open(ConfirmDialog, { data: { title: "Reject request", message: "Are you sure to reject this request?", CANCEL: true, YES: true } })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        this.adminSvc.rejectCompanionRequest(request.lineName, request.direction, request.date, request.username).toPromise()
                            .then(response => this.datasource.reload())
                            .catch(error => console.log(error));
                        break;
                    default:
                        break;
                }
            })
    }

    unAcceptRequest(request:CompanionRequest){
        this.dialog.open(ConfirmDialog, { data: { title: "Remove acceptance", message: "Are you sure to remove the acceptance of the request?", CANCEL: true, YES: true } })
        .afterClosed().subscribe(result => {
            switch (result) {
                case "YES":
                    this.adminSvc.unAcceptCompanionRequest(request.lineName, request.direction, request.date, request.username).toPromise()
                        .then(response => this.datasource.reload())
                        .catch(error => console.log(error));
                    break;
                default:
                    break;
            }
        })
    }

    manageRequest(request:CompanionRequest){
        console.log(request);
        const dialogRef = this.dialog.open(EditRaceDialog, {
            data: {
                lineName:request.lineName,
                date:request.date,
                direction:request.direction
            }
        });
    }
}