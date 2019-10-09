import { Component, OnInit, NgZone } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css', '../auth.style.css']
})
export class LoginComponent implements OnInit {
    form: FormGroup;
    loginError: string;
    showSpinner = false;
    busy = false;

    constructor(private ngZone: NgZone,private authService: AuthService, private router: Router, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Login');
    }

    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    login(): void {
        if (this.form.valid) {
            this.busy = true;
            this.showSpinner = true;
            this.authService.login(this.form.value)
                .then(_ => {
                    console.log("login completato ->navigo verso home");
                    this.ngZone.run(() => this.router.navigate(['../../'])).then();
                })
                .catch(error => {
                    console.log(error);
                    this.loginError = error;
                    this.busy = false;
                    this.showSpinner = false;
                });
        }
    }
}
