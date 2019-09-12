import { Stop } from './stop';
import { UserInfo } from './user';

export enum CompanionState {
    AVAILABLE,
    CHOSEN,
    CONFIRMED,
    VALIDATED
}

export class Companion {
    public userDetails: UserInfo;
    public initialStop: Stop;
    public finalStop: Stop;
    public state: CompanionState;
}

