import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJs from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { AuthService } from '../auth/auth.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { DirectionType, Race } from 'src/app/models/race';
import { EventEmitter } from 'events';
import { Notification } from 'src/app/models/notification';
import { ParentService } from '../parent/parent.service';
import Utils from 'src/app/utils/utils';
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
          !this.stompClient ||
            this.stompClient.disconnect(() =>
              console.log('STOMP client disconnected')
            );
          !this.webSocket || this.webSocket.close();
        }
        if (status) {
          this.connect();
        }
      });
  }

  public notify(notification?: Notification) {
    !this.stompClient ||
      this.stompClient.send(
        '/app/notify',
        null,
        JSON.stringify({
          performerUsername: this.authSvc.getCurrentUser().mail,
          targetUsername: 'movalli@mail.com',
          type: 0,
          date: new Date(),
          message: 'messaggio ',
          isRead: false
        })
      );
  }

  private connect() {
    this.webSocket = new SockJs(WSS_BASE_ENDPOINT);
    this.stompClient = Stomp.over(this.webSocket);
    this.stompClient.connect(
      { Authorization: `Bearer ${this.authSvc.getToken()}` },
      (frame: any) => {
        console.log('WEB SOCKET connected');
        console.log(frame);

        this.connectedSbj.next(true);
        this.userSvc
          .getNotifications(10, 0)
          .then(lastNotifications => {
            this.notifications = [];
            this.unreadSbj.next(0);
            let unread = 0;
            lastNotifications.content.forEach(message => {
              const notification = new Notification();
              notification.sender = message.performerUsername;
              notification.message = message.message;
              const date = new Date(message.date);
              notification.date =
                Utils.getTime(date.getTime()) +
                ', ' +
                date.toLocaleDateString();
              notification.unread = !message.isRead;
              if (notification.unread) unread++;
              this.notifications.push(notification);
            });
            this.unreadSbj.next(unread);
            this.notifier.emit('notification');
          })
          .catch(error => console.log(error))
          .then(() => {
            this.stompClient.subscribe(
              `/user/queue/notifications`,
              (message: any) => {
                message = JSON.parse(message.body);
                console.log(message);
                const notification = new Notification();
                notification.sender = message.performerUsername;
                notification.message = message.message;
                const date = new Date(message.date);
                notification.date =
                  Utils.getTime(date.getTime()) +
                  ', ' +
                  date.toLocaleDateString();
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

  public subscribeToTodayRaces() {
    if (this.connectedSbj.value)
      this.parentSvc
        .getTodayRaces()
        .then((races: Race[]) =>
          races.forEach(race =>
            this.subscribeToRace(race.line.name, race.date, race.direction)
          )
        )
        .catch(error => console.log(error));
  }

  private subscribeToRace(
    lineName: string,
    date: Date,
    direction: DirectionType
  ) {
    this.stompClient.subscribe(
      `/topic/notifications/${lineName}/${date.toISOString()}/${direction}`,
      (notification: any) => {
        this.notifications.push(notification);
        //this.notifications.sort((a: Notification, b: Notification) => b.date.getTime() - a.date.getTime());
        this.unreadSbj.next(this.unreadSbj.getValue() + 1);
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
    if (notification.unread) {
      notification.unread = false;
      this.unreadSbj.next(this.unreadSbj.getValue() - 1);
    }
  }
}
