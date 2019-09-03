import { Child } from './child';
import { UserRole } from './roles';

export class UserInfo {
    public mail: string;
    public name: string;
    public surname: string;
    public contacts: string[];
    public children: Child[];
    public adminlines: string[];
    public companionLines: string[];
    public roles: UserRole[];

    public isAdmin(): boolean {
        console.log("ISADMIN")
        console.log(this.getRoles());
        return this.roles != null && this.roles.find(r => r === UserRole.ROLE_ADMIN || r === UserRole.ROLE_SYSTEM_ADMIN) != null;
    }

    public isCompanion(): boolean {
        return this.roles != null &&
            this.roles.find(r => r === UserRole.ROLE_COMPANION || r === UserRole.ROLE_SYSTEM_ADMIN) != null;
    }

    public isSystemAdmin(): boolean {
        return this.roles != null &&
            this.roles.find(r => r === UserRole.ROLE_SYSTEM_ADMIN) != null;
    }

    public getRoles(){
        console.log(JSON.stringify(this))
        return this.roles;
    }
}
