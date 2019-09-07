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
        return this.roles != null && this.roles.find(r => r === UserRole.ADMIN || r === UserRole.SYSTEM_ADMIN) != null;
    }

    public isCompanion(): boolean {
        return this.roles != null &&
            this.roles.find(r => r === UserRole.COMPANION || r === UserRole.SYSTEM_ADMIN) != null;
    }

    public isSystemAdmin(): boolean {
        return this.roles != null &&
            this.roles.find(r => r === UserRole.SYSTEM_ADMIN) != null;
    }
}
