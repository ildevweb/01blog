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
    console.log('LOGIN JSON:', this.loginData);

    this.http.post(
      'http://localhost:8080/api/auth/login',
      this.loginData
    ).subscribe({
      next: res => console.log('Login success', res),
      error: err => console.error(err)
    });
  }

  onRegister() {
    if (this.registerData.password !== this.registerData.confirmPassword) {
      alert('Passwords do not match');
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
      next: res => console.log('Register success', res),
      error: err => console.error(err)
    });
  }
}
