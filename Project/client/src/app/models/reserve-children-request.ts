import { Race } from './race';
import { Passenger } from './passenger';

export class reserveChildrenRequest {
    clientRace: Race;
    children: Passenger[];
}