import { Race } from './race';

export enum NotificationType {
    CHILD_TAKEN,
    CHILD_ABSENT,
    CHILD_PRESENT,
    CHILD_DELIVERED,
    ROUND_CONFIRM,
    ROUND_DELETED,
    RACE_STARTED,
    RACE_ENDED
}

export class Notification {
    date: string;
    sender: string;
    targetUser: string;
    targetRace: Race;
    message: string;
    type: NotificationType;
    unread: boolean;
}