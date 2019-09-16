import { Stop } from './stop';
import { Child } from './child';

export enum PassengerState {
    ABSENT,
    TAKEN,
    DELIVERED,
    PRESENT,
    NULL
}

export class Passenger {
    public childDetails: Child;
    public stopReserved: Stop;
    public stopTaken: Stop;
    public stopDelivered: Stop;
    public reserved: Boolean;
    public state: PassengerState;
}
