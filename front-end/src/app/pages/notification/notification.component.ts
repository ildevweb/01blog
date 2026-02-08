import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { NotificationService } from '../../core/services/notification.service';


@Component({
  selector: 'app-notifications',
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './notification.component.html',
})
export class NotificationComponent implements OnInit {

  notifications = new BehaviorSubject<any[]>([]);

  private readonly notifications_API = 'http://localhost:8080/api/notifications';

  constructor(private notificationService: NotificationService, private http: HttpClient,) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.http
    .get<any[]>(`${this.notifications_API}/unread/get`)
    .subscribe({
        next: res => {
            console.log("this is unreaded notifications :", res);
            this.notifications.next(res);
        },
        error: () => {
            console.log("failed getting notifications");
        }
    });
  }

  markAllAsRead() {
    this.http
      .get(`${this.notifications_API}/unread/mark_all_as_read`)
      .subscribe({
          next: () => {
            console.log("mark all as read successfully");
            const updated = this.notifications.value.map(notif => ({ ...notif, readed: true }));
            this.notifications.next(updated);
            this.notificationService.unreadCountSubject.next(0);
            this.notificationService.refreshUnreadCount();
          },
          error: () => {
            console.log("mark all as read failed");
          }
      });
  }
}


