import { Component, OnInit } from "@angular/core";
import { AuthService } from 'src/app/services/auth/auth.service';

@Component({
    template:"Logging out..."
})
export class LogoutComponent implements OnInit{
    constructor(private auth : AuthService){
    }

    ngOnInit(){
        this.auth.logout();
    }
}