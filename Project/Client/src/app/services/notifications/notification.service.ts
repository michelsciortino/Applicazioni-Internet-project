import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJs from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { AuthService } from '../auth/auth.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { DirectionType, Race } from 'src/app/models/race';
import { EventEmitter } from 'events';
import { Notification, IncomingNotification } from 'src/app/models/notification';
import { ParentService } from '../parent/parent.service';
import { UserService } from '../user/user.service';

const WSS_BASE_ENDPOINT = environment.baseWsEndpoint;

@Injectable()
export class NotificationService {
  private webSocket: WebSocket;
  private stompClient: Stomp.Client;
  private connectedSbj: BehaviorSubject<boolean>;
  private unreadSbj: BehaviorSubject<number>;
  private notifications: any[];
  private notifier: EventEmitter;

  private loggedStatusSub: Subscription;

  constructor(
    private parentSvc: ParentService,
    private authSvc: AuthService,
    private userSvc: UserService
  ) {
    this.connectedSbj = new BehaviorSubject(false);
    this.unreadSbj = new BehaviorSubject(0);
    this.notifications = [];
    this.notifier = new EventEmitter();
    this.loggedStatusSub = this.authSvc
      .observeLoggedStatus()
      .subscribe(status => {
        if (this.connectedSbj.getValue()) {
          !this.stompClient || this.stompClient.disconnect(() => console.log('STOMP client disconnected'));
          !this.webSocket || this.webSocket.close();
        }
        !status || this.connect();
      });
  }

  private connect() {
    this.webSocket = new SockJs(WSS_BASE_ENDPOINT);
    this.stompClient = Stomp.over(this.webSocket);
    this.stompClient.connect(
      { Authorization: `Bearer ${this.authSvc.getToken()}` },
      (frame: any) => {
        console.log('WEB SOCKET connected');
        // console.log(frame);

        this.connectedSbj.next(true);
        this.userSvc
          .getNotifications(10, 0)
          .then(lastNotifications => {
            this.notifications = [];
            this.unreadSbj.next(0);
            let unread = 0;
            lastNotifications.content.forEach(
              (incomingN: IncomingNotification) => {
                const notification = new Notification();
                notification.id = incomingN.performerUsername;
                notification.sender = incomingN.performerUsername;
                notification.targetUser = incomingN.targetUsername;
                notification.targetRace = incomingN.referredRace;
                notification.type = incomingN.type;
                notification.message = incomingN.message;
                notification.date = incomingN.date;
                notification.unread = !incomingN.isRead;
                if (notification.unread) unread++;
                this.notifications.push(notification);
              }
            );
            this.unreadSbj.next(unread);
            this.notifier.emit('notification');
          })
          .catch(error => console.log(error))
          .then(() => {
            this.stompClient.subscribe(`/user/queue/notifications`,
              (message: any) => {
                const incomingN: IncomingNotification = JSON.parse(
                  message.body
                );
                const notification = new Notification();
                notification.id = incomingN.id;
                notification.targetRace = incomingN.referredRace;
                notification.targetUser = incomingN.targetUsername;
                notification.type = incomingN.type;
                notification.sender = incomingN.performerUsername;
                notification.message = incomingN.message;
                notification.date = incomingN.date;
                notification.unread = true;
                this.notifications.push(notification);
                this.unreadSbj.next(this.unreadSbj.getValue() + 1);
                this.notifier.emit('notification');
              }
            );
          })
          .catch(error => console.log(error));
      }
    );
  }

  public listen(listener) {
    this.notifier.addListener('notification', listener);
  }

  public stopListening(listener) {
    this.notifier.removeListener('notification', listener);
  }

  public subscribeToRace(lineName: string, date: Date, direction: DirectionType) {
    return this.stompClient.subscribe(`/topic/notifications/${lineName}/${date.toISOString()}/${direction}`,
      (message: Stomp.Message) => {
        const incomingN = JSON.parse(message.body) as IncomingNotification;
        const notification = new Notification();
        notification.id = incomingN.id;
        notification.date = incomingN.date;
        notification.message = incomingN.message;
        notification.sender = incomingN.performerUsername;
        notification.targetRace = incomingN.referredRace;
        notification.type = incomingN.type;
        notification.unread = !incomingN.isRead;
        this.notifications.push(notification);
        return notification;
      }
    );
  }

  public getNotifications() {
    return this.notifications;
  }

  public getUnreadCount() {
    return this.unreadSbj.asObservable();
  }

  public getConnectionStatus() {
    return this.connectedSbj.asObservable();
  }

  public readNotification(notification: Notification) {
    if (notification.unread)
      this.userSvc
        .readNotification(notification.id)
        .then(() => {
          notification.unread = false;
          this.unreadSbj.next(this.unreadSbj.getValue() - 1);
        })
        .catch(error => console.log(error));
  }
}
