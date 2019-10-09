import { DirectionType } from './race';
import { Stop } from './stop';
import { CompanionState } from './companion';

export class CompanionRequest {
    username: string;
    lineName: string;
    direction: DirectionType;
    date: Date;
    initialStop: Stop;
    finalStop: Stop;
    state: CompanionState;
}