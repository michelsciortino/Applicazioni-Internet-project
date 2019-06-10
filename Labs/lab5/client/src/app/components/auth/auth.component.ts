import { Component, OnInit } from "@angular/core";
import { AuthService } from 'src/app/services/auth/auth.service';
import { take, map } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
    selector: "auth",
    templateUrl: "./auth.component.html",
    styleUrls: ["./auth.style.css"]
})
export class AuthComponent implements OnInit {

    constructor(private auth: AuthService, private router: Router) { }

    ngOnInit(): void {
    }
}