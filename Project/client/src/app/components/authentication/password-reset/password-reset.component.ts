import { Component, OnInit } from '@angular/core';
import { AuthService as authService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRoute } from '@angular/router';
import { MessageService } from 'src/app/services/bridges/message.service';

@Component({
    selector: 'app-pass-reset',
    templateUrl: 'password-reset.component.html',
    styleUrls: ['password-reset.component.css', '../auth.style.css']
})
export class PasswordResetComponent implements OnInit {
    form: FormGroup;
    showSpinner = false;
    errorMessage = '';
    busy = false;

    token: string;

    constructor(private authSvc: authService, private msgSvc: MessageService, private router: Router, private route: ActivatedRoute, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Reset Password');
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.token = params.token;
            console.log('token is:', this.token);
        });
        this.form = this.fb.group({
            password1: ['', Validators.compose([Validators.required])],
            password2: ['', Validators.compose([Validators.required])]
        }, { validator: this.checkPasswords });
    }

    private checkPasswords(group: FormGroup) {
        const pass1 = group.controls.password1;
        const pass2 = group.controls.password2;

        if (!pass1 || !pass2 || pass1.invalid || pass2.invalid) {
            return null;
        }

        return (pass1.valid && pass2.valid && pass1.value === pass2.value) ?
            null : { passowordsNotEqual: true }
    }

    reset() {
        this.busy = true;
        this.errorMessage = null;
        this.showSpinner = true;
        this.authSvc.resetPassword(this.token, this.form.value.password1)
            .then(_ => {
                this.msgSvc.title = 'Password Reset';
                this.msgSvc.message = `Your password has been succesfully reset.`;
                this.router.navigate([`${this.router.url}/done`]);
            })
            .then(_ => { })
            .catch((error) => {
                this.errorMessage = error;
                this.busy = false;
            })
            .then(_ => this.showSpinner = false);
    }
}
