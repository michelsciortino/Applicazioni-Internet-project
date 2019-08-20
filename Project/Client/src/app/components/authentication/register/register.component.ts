import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css', '../auth.style.css']
})
export class RegisterComponent implements OnInit {
    form: FormGroup;
    showMailSentMessage = false;
    showSpinner = false;
    mail: string;
    errorMessage = '';
    busy = false;

    constructor(private authSvc: AuthService, private router: Router, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Register');
    }

    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', Validators.compose([Validators.required, Validators.email])]
        });
    }

    register() {
        this.busy = true;
        this.errorMessage = null;
        this.showSpinner = true;
        this.showMailSentMessage = false;
        this.authSvc.register(this.form.value.mail)
            .then(_ => this.showMailSentMessage = true)
            .catch((error) => {
                this.errorMessage = error;
                this.busy = false;
            })
            .then(_ => this.showSpinner = false);
    }
}
