import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './auth.component.html',
})
export class AuthComponent {
  isLogin = true;
  errorMessage$ = new BehaviorSubject<string | null>(null);
  successMessage$ = new BehaviorSubject<string | null>(null);

  validateEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  validateUsername(username: string): boolean {
    // Only letters, numbers, underscores, 3-20 chars
    const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
    return usernameRegex.test(username);
  }

  validatePassword(password: string): boolean {
    // Minimum 8 chars, at least 1 letter and 1 number
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
    return passwordRegex.test(password);
  }

  // Login data
  loginData = {
    email: '',
    password: ''
  };

  // Register data
  registerData = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  constructor(private http: HttpClient) {}

  showLogin() {
    this.isLogin = true;
  }

  showRegister() {
    this.isLogin = false;
  }

  onLogin() {
    if (this.loginData.email == '' || this.loginData.password == '') {
      this.errorMessage$.next('Some input is empty');
      //setTimeout(() => this.errorMessage = '', 100);
      return;
    }

    console.log('LOGIN JSON:', this.loginData);

    this.http.post(
      'http://localhost:8080/api/auth/login',
      this.loginData
    ).subscribe({
      next: res => {
        console.log('Login success', res)
        this.successMessage$.next("Logged Successfully")
        this.errorMessage$.next(null)
      },
      error: err => {
        this.errorMessage$.next(err.error?.message || 'Login failed')
        this.successMessage$.next(null)
      }
    });
  }

  onRegister() {
    if (this.registerData.email == '' || this.registerData.username == '' || this.registerData.password == '' || this.registerData.confirmPassword == '') {
      this.errorMessage$.next('Some input is empty');
      return;
    }

    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.errorMessage$.next('Passwords do not match');
      return;
    }

    if (!this.validateEmail(this.registerData.email)) {
      this.errorMessage$.next('Invalid email');
      return;
    }
    if (!this.validateUsername(this.registerData.username)) {
      this.errorMessage$.next('Invalid username');
      return;
    }
    if (!this.validatePassword(this.registerData.password)) {
      this.errorMessage$.next('Invalid password');
      return;
    }

    const payload = {
      username: this.registerData.username,
      email: this.registerData.email,
      password: this.registerData.password
    };

    console.log('REGISTER JSON:', payload);

    this.http.post(
      'http://localhost:8080/api/auth/register',
      payload
    ).subscribe({
      next: res => {
        console.log('Register success', res)
        this.errorMessage$.next(null)
        this.successMessage$.next("Registered Successfully")
      },
      error: err => {
        console.log("this is the error:", err)
        this.errorMessage$.next(err.error?.message || 'Registration failed')
        this.successMessage$.next(null)
      }
    });
  }
}
