import { Stop } from './stop';
import { UserInfo } from './user';

export enum CompanionState {
    AVAILABLE = "AVAILABLE",
    CHOSEN = "CHOSEN",
    CONFIRMED = "CONFIRMED",
    VALIDATED = "VALIDATED"
}

export class Companion {
    public userDetails: UserInfo;
    public initialStop: Stop;
    public finalStop: Stop;
    public state: CompanionState;
}

