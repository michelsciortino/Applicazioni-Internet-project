import { Component, Input, OnInit } from '@angular/core';
import { MessageService } from 'src/app/services/bridges/message.service';

@Component({
    selector: 'app-message',
    templateUrl: 'message.component.html',
    styleUrls: ['message.component.css']
})
export class MessageComponent implements OnInit {

    title: string;
    message: string;

    @Input()
    nextRoute: string;

    constructor(private messageSvc: MessageService) { }

    ngOnInit() {
        this.title = this.messageSvc.title;
        this.message = this.messageSvc.message;
    }

}
