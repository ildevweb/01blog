import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth.component.html',
})
export class AuthComponent {
  isLogin = true;

  showLogin() {
    this.isLogin = true;
  }

  showRegister() {
    this.isLogin = false;
  }
}
