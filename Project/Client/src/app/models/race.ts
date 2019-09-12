import { Passenger } from './passenger';
import { Companion } from './companion';

export class Race {
    public lineName: string;
    public direction: string;
    public date: Date;
    public passengers: Passenger[];
    public Companions: Companion[];
}

export enum DirectionType {
    OUTWARD="OUTWARD",
    RETURN="RETURN"
}