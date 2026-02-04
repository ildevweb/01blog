import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/auth/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  imports: [CommonModule, RouterModule],
})
export class NavbarComponent implements OnInit {

  unreadCount = 0;
  isAdmin = new BehaviorSubject<boolean | undefined>(undefined);

  constructor(
    private notificationService: NotificationService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.notificationService.loadUnreadCount();

    this.notificationService.getUnreadCount()
      .subscribe(count => this.unreadCount = count);

    this.checkAdmin();
  }

  getUserRole(): string | null {
    const token = localStorage.getItem('token');
    if (!token) return null;

    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role;
  }

  checkAdmin() {
    this.isAdmin.next(this.getUserRole() === 'admin');
  }


  //this is for logout
  logout() {
    localStorage.removeItem(('token'));
    this.router.navigate(['/auth']);
  }
}
