import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { NotificationService } from '../../core/services/notification.service';


interface Notification {
  id: number;
  username: string;
  content: string;
  time: string;
  avatar: string;
  read: boolean;
}

@Component({
  selector: 'app-notifications',
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './notification.component.html',
})
export class NotificationComponent implements OnInit {

  notifications = new BehaviorSubject<any[]>([]);

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.loadNotifications();

    this.notificationService.getNotifications()
    .subscribe(result => this.notifications.next(result));
  }


  markAllAsRead() {
    const updated = this.notifications.value.map(notif => ({ ...notif, readed: true }));
    this.notifications.next(updated);
  }
}


