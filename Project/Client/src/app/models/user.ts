import { Child } from './child';
import { UserRole } from './roles';

export class UserInfo {
    public mail: string;
    public name: string;
    public surname: string;
    public contacts: string[];
    public children: Child[];
    public lines: string[];
    public roles: UserRole[];

    constructor(init?:Partial<UserInfo>){
        Object.assign(this,init);
    }

    public isAdmin(): boolean {
        return this.roles != null && this.roles.find(r => r === UserRole.ADMIN || r === UserRole.SYSTEM_ADMIN) != null;
    }

    public isCompanion(): boolean {
        return this.roles != null &&
        this.roles.find(r => r === UserRole.COMPANION) != null;
    }

    public isSystemAdmin(): boolean {
        return this.roles != null &&
        this.roles.find(r => r === UserRole.SYSTEM_ADMIN) != null;
    }
}
