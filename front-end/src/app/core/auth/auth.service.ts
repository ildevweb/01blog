/* Exposes methods: isLoggedIn(), getToken(), logout() */
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private TOKEN_KEY = 'token';

  constructor() {}

  // Save JWT
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  // Get JWT
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Remove JWT
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  // Check if token exists and is not expired
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    return !this.isTokenExpired(token);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch (e) {
      return true;
    }
  }
}
