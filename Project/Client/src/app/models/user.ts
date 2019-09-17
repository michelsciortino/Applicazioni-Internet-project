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

    public isAdmin(userinfo:UserInfo): boolean {
        return userinfo.roles != null && userinfo.roles.find(r => r === UserRole.ADMIN || r === UserRole.SYSTEM_ADMIN) != null;
    }

    public isCompanion(userinfo:UserInfo): boolean {
        return userinfo.roles != null &&
        userinfo.roles.find(r => r === UserRole.COMPANION) != null;
    }

    public isSystemAdmin(userinfo:UserInfo): boolean {
        return userinfo.roles != null &&
        userinfo.roles.find(r => r === UserRole.SYSTEM_ADMIN) != null;
    }
}
