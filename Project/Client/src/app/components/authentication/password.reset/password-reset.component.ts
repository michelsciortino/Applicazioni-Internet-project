import { Component, OnInit } from '@angular/core';
import { AuthService as authService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';

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

    constructor(private authSvc: authService, private router: Router, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Reset Password');
    }

    ngOnInit() {
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
        this.authSvc.resetPassword(this.form.value.password1)
            .then(_ => { })
            .catch((error) => {
                this.errorMessage = error;
                this.busy = false;
            })
            .then(_ => this.showSpinner = false);
    }
}