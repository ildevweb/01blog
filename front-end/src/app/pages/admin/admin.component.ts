import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';


@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    adminData = new BehaviorSubject<any>(null);

    private readonly postAPI = 'http://localhost:8080/api/post';
    private readonly commentAPI = 'http://localhost:8080/api/comment';
    private readonly userAPI = 'http://localhost:8080/api/user';
    private readonly adminAPI = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient) {}

    ngOnInit(): void {
        this.getData();
    }

    private getData() {
        this.http.get(`${this.adminAPI}/getData`).subscribe({
            next: data => {
                console.log("Admin data:", data);
                this.adminData.next(data);
            },
            error: err => {
                console.error('Failed to load admin data:', err);
            }
        });
    }
}