import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { Router } from '@angular/router';


@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    adminData = new BehaviorSubject<any>(null);

    users = new BehaviorSubject<any[]>([]);
    posts = new BehaviorSubject<any[]>([]);
    reports = new BehaviorSubject<any[]>([]);

    private readonly postAPI = 'http://localhost:8080/api/post';
    private readonly userAPI = 'http://localhost:8080/api/user';
    private readonly adminAPI = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient, private router: Router) {}

    ngOnInit(): void {
        this.getData();
        this.getUsers();
        this.getPosts();
        this.getReports();
    }

    //get static data
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

    //get users
    getUsers(): void {
        this.http.get<any[]>(`${this.userAPI}/all`)
        .subscribe({
            next: users => {
            console.log("this is the whole users:", users);
            this.users.next(users);
            },
            error: err => {
            console.log('Failed to load users:', err);
            }
        });
    }

    //redirect to user profile
    goToProfile(id: number) {
        this.router.navigate(['/profile', id]);
    }

    //send request to delete user
    deleteUser(userId: number) {
        this.http.get(`${this.userAPI}/delete/${userId}`).subscribe({
            next: () => {
                console.log("success delete user:");
                this.getUsers();
                this.getReports();
            },
            error: err => {
                console.error('Failed to delete user :', err);
            }
        });
    }

    //send request to ban / unban user
    toggleBan(userId: number) {
        this.http.get(`${this.userAPI}/ban/${userId}`).subscribe({
            next: () => {
                console.log("success ban / unban user:");
                this.getUsers();
            },
            error: err => {
                console.error('Failed to ban / unban user :', err);
            }
        });
    }


    //get posts
    getPosts() {
        this.http.get<any[]>(`${this.postAPI}/all`)
        .subscribe({
            next: posts => {
            console.log("this is the whole posts:", posts);
            this.posts.next(posts);
            },
            error: err => {
            console.log('Failed to load posts:', err);
            }
        });
    }

    //send request to delete post
    deletePost(postId: number) {
        this.http.get(`${this.postAPI}/delete/${postId}`).subscribe({
            next: () => {
                console.log("success delete post:");
                this.getPosts();
                this.getReports();
            },
            error: err => {
                console.error('Failed to delete post :', err);
            }
        });
    }

    //send request to ban / unban post
    togglePostBan(postId: number) {
        this.http.get(`${this.postAPI}/ban/${postId}`).subscribe({
            next: () => {
                console.log("success ban / unban post:");
                this.getPosts();
            },
            error: err => {
                console.error('Failed to ban / unban post :', err);
            }
        });
    }

    //get reports
    getReports() {
        this.http.get<any[]>(`${this.adminAPI}/reports`)
        .subscribe({
            next: reports => {
                console.log("this is the whole reports:", reports);
                this.reports.next(reports);
            },
            error: err => {
                console.log('Failed to load reports:', err);
            }
        });
    }

    //dismiss report
    dismissReport(reportId: number) {
        const confirmed = window.confirm('Are you sure you want to dismiss this report?');
        if (!confirmed) {
            return;
        }

        this.http.get(`${this.adminAPI}/dismiss/${reportId}`).subscribe({
            next: () => {
                console.log("success dismiss report");
                this.getReports();
                this.getData();
            },
            error: err => {
                console.error('Failed to dismiss report :', err);
            }
        });
    }

    //delete user or post from report
    delete(id: number, type: string) {
        const confirmed = window.confirm('Are you sure you want to delete this reported?');
        if (!confirmed) {
            return;
        }

        if (type == "user") {
            this.deleteUser(id);
        } else if (type == "post") {
            this.deletePost(id);
        }
    }
}