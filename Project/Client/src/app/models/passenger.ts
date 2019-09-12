import { Stop } from './stop';
import { UserInfo } from './user';

export enum PassengerState {
    ABSENT,
    TAKEN,
    DELIVERED,
    PRESENT,
    NULL
}

export class Passenger {
    public childDetails: UserInfo;
    public stopReserved: Stop;
    public stopTaken: Stop;
    public stopDelivered: Stop;
    public isReserved: Boolean;
    public state: PassengerState;
}
