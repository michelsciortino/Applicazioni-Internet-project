import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ConfirmMail } from 'src/app/models/requests/confirmMail';
import { MessageService } from 'src/app/services/bridges/message.service';

@Component({
    selector: 'app-confirm',
    templateUrl: './confirm.component.html',
    styleUrls: ['./confirm.component.css', '../auth.style.css']
})
export class ConfirmComponent implements OnInit {
    form: FormGroup;
    showSpinner = false;
    errorMessage = '';
    busy = false;

    token: string;

    constructor(private authSvc: AuthService, private msgSvc: MessageService, private router: Router, private route: ActivatedRoute, private fb: FormBuilder, private titleService: Title) {
        this.titleService.setTitle('Confirm');
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.token = params.token;
            console.log('token is:', this.token);
        });

        this.form = this.fb.group({
            name: ['', Validators.required],
            surname: ['', Validators.required],
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

    confirm() {
        this.busy = true;
        this.errorMessage = null;
        this.showSpinner = true;
        const confirmMail = new ConfirmMail(this.form.value.name, this.form.value.surname, this.form.value.password1);

        this.authSvc.confirmMail(this.token, confirmMail)
            .then(_ => {
                this.msgSvc.title = 'Account confimation';
                this.msgSvc.message = `Your account has been succesfully created :)`;
                this.router.navigate([`${this.router.url}/done`]);
            })
            .catch((error) => {
                this.errorMessage = error;
                this.busy = false;
            })
            .then(_ => this.showSpinner = false);
    }
}
