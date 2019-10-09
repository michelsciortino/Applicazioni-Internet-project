import { Stop } from './stop';
import { Child } from './child';

export enum PassengerState {
    ABSENT = "ABSENT",
    TAKEN = "TAKEN",
    DELIVERED = "DELIVERED",
    PRESENT = "PRESENT",
    NULL = "NULL"
}

export class Passenger {
    public childDetails: Child;
    public stopReserved: Stop;
    public stopTaken: Stop;
    public stopDelivered: Stop;
    public reserved: Boolean;
    public state: PassengerState;
}
