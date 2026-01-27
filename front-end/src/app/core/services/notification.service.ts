import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private unreadCountSubject = new BehaviorSubject<number>(0);
  private loaded = false;

  private readonly API_URL = 'http://localhost:8080/api/notifications';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  loadUnreadCount(): void {
    if (this.loaded) return;
    if (!this.authService.isLoggedIn()) return;

    this.loaded = true;

    this.http
      .get<any>(`${this.API_URL}/unread/count`)
      .subscribe({
        next: res => this.unreadCountSubject.next(res.count),
        error: () => {
          this.loaded = false;
        }
      });
  }

  getUnreadCount(): Observable<number> {
    return this.unreadCountSubject.asObservable();
  }

  setUnreadCount(count: number): void {
    this.unreadCountSubject.next(count);
  }

  refreshUnreadCount(): void {
    this.loaded = false;
    this.loadUnreadCount();
  }

  /**
   * Reset state (call on logout)
   */
  reset(): void {
    this.loaded = false;
    this.unreadCountSubject.next(0);
  }
}
