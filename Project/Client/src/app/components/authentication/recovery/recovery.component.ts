import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'src/app/services/bridges/message.service';

@Component({
    selector: 'app-recovery',
    templateUrl: './recovery.component.html',
    styleUrls: ['./recovery.component.css', '../auth.style.css']
})
export class RecoveryComponent implements OnInit {
    form: FormGroup;
    showMailSentMessage = false;
    showSpinner = false;
    mail: string;
    errorMessage = '';
    busy = false;

    constructor(private authSvc: AuthService, private msgSvc: MessageService, private router: Router, private route: ActivatedRoute, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Recovery');
    }

    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', Validators.compose([Validators.required, Validators.email])]
        });
    }

    recovery() {
        this.busy = true;
        this.errorMessage = null;
        this.showSpinner = true;
        this.showMailSentMessage = false;

        this.authSvc.recovery(this.form.value.mail)
            .then(_ => {
                this.msgSvc.title = 'Recovery';
                this.msgSvc.message = `A recovery mail has been sent to ${this.form.value.mail}`;
                this.router.navigate([`${this.router.url}/done`]);
            })
            .catch((error) => {
                this.errorMessage = error;
                this.busy = false;
            })
            .then(_ => this.showSpinner = false);
    }
}
