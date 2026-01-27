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

    constructor(private notificationService: NotificationService) {}

    ngOnInit(): void {
        //console.log("this is the notifications :", this.notificationService.getNotifications());
        this.notificationService.getNotifications()
        .subscribe(result => console.log("this is the notifications :", result));
    }

  notifications$ = new BehaviorSubject<Notification[]>([
    { id: 1, username: 'John Doe', content: 'liked your post', time: '2 hours ago', avatar: 'https://via.placeholder.com/40', read: false },
    { id: 2, username: 'Jane Smith', content: 'commented: "Great work!"', time: '5 hours ago', avatar: 'https://via.placeholder.com/40', read: false },
    { id: 3, username: 'Mike Johnson', content: 'started following you', time: '1 day ago', avatar: 'https://via.placeholder.com/40', read: false },
  ]);

  markAllAsRead() {
    const updated = this.notifications$.value.map(notif => ({ ...notif, read: true }));
    this.notifications$.next(updated);
  }
}


