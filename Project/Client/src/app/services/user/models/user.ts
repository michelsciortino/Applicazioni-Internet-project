import { Child } from './child';
import { UserRole } from './roles';

export class UserInfo {
    mail: string;
    name: string;
    surname: string;
    contacts: string[];
    children: Child[];
    adminlines: string[];
    companionLines: string[];
    roles: UserRole[];
}
