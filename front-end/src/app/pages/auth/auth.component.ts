import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './auth.component.html',
})
export class AuthComponent {
  isLogin = true;
  errorMessage: string = '';

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
      this.errorMessage = 'Some input is empty';
      setTimeout(() => this.errorMessage = '', 100);
      return;
    }

    console.log('LOGIN JSON:', this.loginData);

    this.http.post(
      'http://localhost:8080/api/auth/login',
      this.loginData
    ).subscribe({
      next: res => console.log('Login success', res),
      error: err => this.errorMessage = err.error?.message || 'Login failed'
    });
  }

  onRegister() {
    if (this.registerData.email == '' || this.registerData.username == '' || this.registerData.password == '' || this.registerData.confirmPassword == '') {
      this.errorMessage = 'some input is empty';
      setTimeout(() => this.errorMessage = '', 100);
      return;
    }

    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      setTimeout(() => this.errorMessage = '', 100);
      return;
    }

    /*if (!this.validateEmail(this.registerData.email) || !this.validateUsername(this.registerData.username) || !this.validatePassword(this.registerData.password)) {
      this.errorMessage = 'Invalid data';
      setTimeout(() => this.errorMessage = '', 100);
      return;
    }*/

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
      next: res => console.log('Register success', res),
      error: err => this.errorMessage = err.error?.message || 'Registration failed'
    });
  }
}
