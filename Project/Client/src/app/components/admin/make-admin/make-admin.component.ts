import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth/auth.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'src/app/services/bridges/message.service';

@Component({
    selector: 'app-make-admin',
    templateUrl: './make-admin.component.html',
    styleUrls: ['./make-admin.component.css','../admin.component.css']
})
export class MakeAdminComponent implements OnInit {
    form: FormGroup;
    mail: string;

    constructor(private authSvc: AuthService, private msgSvc: MessageService, private router: Router, private fb: FormBuilder, private titleService: Title) {
        //this.titleService.setTitle('makeAdmin');
    }

    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', Validators.compose([Validators.required, Validators.email])]
        });
    }

    makeCompanion() {
       //TODO: complete
       console.log("Make a admin...");
    }
}
