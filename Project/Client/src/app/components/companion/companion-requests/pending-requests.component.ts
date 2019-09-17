import { Component } from "@angular/core";
import { CompanionRequest } from 'src/app/models/companion-request';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';
import { CompanionRequestsDataSource, CompanionService } from 'src/app/services/companion/companion.service';


enum RequestsType {
    PENDING,
    ACCEPTED,
    CONFIRMED
}

@Component({
    selector: 'app-companion-pending-requests',
    templateUrl: './pending-requests.component.html',
    styleUrls: ['pending-requests.component.css']
})
export class PendingRequestsComponent {
    //dataSource: RequestsDataSource;

    RequestsTab = RequestsType;
    activeTab: RequestsType;
    requestsSub: Subscription;

    requests: CompanionRequest[];

    datasource: CompanionRequestsDataSource;


    columnDefinitions = ["line", "direction", "date", "initialStop", "finalStop"];


    constructor(private companionSvc: CompanionService, private dialog: MatDialog) {
        this.requests = [];
        this.activeTab = RequestsType.PENDING;
        this.datasource = new CompanionRequestsDataSource(this.companionSvc);
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

    confirmAvailability(request: CompanionRequest) {
        this.dialog.open(ConfirmDialog, { data: { title: "Give avaibility", message: "Are you sure to give your availabiliy?", YES: true, CANCEL: true } })
        .afterClosed().subscribe(result => {
            switch (result) {
                case "YES":
                    this.companionSvc.confirmAvailability(request.lineName, request.direction, request.date).toPromise()
                        .then(response => this.datasource.reload())
                        .catch(error => console.log(error));
                    break;
                default:
                    break;
            }
        });
    }

    cancelRequest(request: CompanionRequest) {
        this.dialog.open(ConfirmDialog, { data: { title: "Cancel request", message: "Are you sure to cancel your availabiliy?", NO: true, YES: true } })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        this.companionSvc.cancelRequest(request.lineName, request.direction, request.date).toPromise()
                            .then(response => this.datasource.reload())
                            .catch(error => console.log(error));
                        break;
                    default:
                        break;
                }
            })
    }
}