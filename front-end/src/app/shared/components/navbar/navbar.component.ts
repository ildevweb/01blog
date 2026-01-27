import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/auth/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  imports: [CommonModule, RouterModule],
})
export class NavbarComponent implements OnInit {

  unreadCount = 0;

  constructor(
    private notificationService: NotificationService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.notificationService.loadUnreadCount();

    this.notificationService.getUnreadCount()
      .subscribe(count => this.unreadCount = count);
  }
}
