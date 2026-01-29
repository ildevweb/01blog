import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

    unreadCountSubject = new BehaviorSubject<number>(0);
    private loaded = false;

    private readonly notifications_API = 'http://localhost:8080/api/notifications';

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {}

    loadUnreadCount(): void {

        this.http
        .get<any>(`${this.notifications_API}/unread/count`)
        .subscribe({
            next: res => {
                this.unreadCountSubject.next(res.count);
            },
            error: err => {
                console.log("error getting notification count :", err);
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

    
    reset(): void {
        this.loaded = false;
        this.unreadCountSubject.next(0);
    }
}
