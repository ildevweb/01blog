import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

declare var bootstrap: any;

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  posts$ = new BehaviorSubject<any[]>([]);
  isLoading = false;

  selectedPost: any = null;
  comments$ = new BehaviorSubject<any[]>([]);
  commentErrorMessage$ = new BehaviorSubject<string | null>(null);

  profileData$ = new BehaviorSubject<any>(null);

  mine$ = new BehaviorSubject<boolean | undefined>(undefined);


  //comment content
  commentData = {
    content: '',
    postId: 0
  }
  isSubmittingComment: boolean = false;

  userId?: number; // Profile owner ID

  private readonly postAPI = 'http://localhost:8080/api/post';
  private readonly commentAPI = 'http://localhost:8080/api/comment';
  private readonly userAPI = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    // Get optional ID from route
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.userId = Number(idParam);
        this.loadUserProfile(this.userId);
        this.mine$.next(false);
      } else {
        this.loadMyProfile();
        this.mine$.next(true);
      }
    });
  }

  // Fetch user profile by ID
  private loadUserProfile(id: number) {
    this.http.get(`${this.userAPI}/profile/${id}`).subscribe({
      next: data => {
        console.log("User profile data:", data);
        this.profileData$.next(data);
        this.userId = id;
        this.fetchPosts(this.userId);
      },
      error: err => {
        console.error('Failed to load user profile:', err);
        this.profileData$.next(null)
        this.router.navigate(['/notfound']);
      }
    });
  }

  // Fetch logged-in user profile
  private loadMyProfile() {
    this.http.get(`${this.userAPI}/profile/me`).subscribe({
      next: (data: any) => {
        console.log("My profile data:", data);
        this.profileData$.next(data);
        this.userId = data.id;
        this.fetchPosts(); // fetch my posts
      },
      error: err => {
        console.error('Failed to load my profile:', err);
        this.profileData$.next(null);
        this.router.navigate(['/notfound']);
      }
    });
  }

  //Follow system
  follow(userId: number) {

    this.http.post<any>(`${this.userAPI}/follow`, { userId: userId })
      .subscribe({
        next: res => {
          console.log("this is the follow res :", res);
          this.loadUserProfile(userId);
        },
        error: err => console.error("Follow failed:", err)
      });
  }

  // Fetch posts for userId or logged-in user
  fetchPosts(userId?: number): void {
    this.isLoading = true;

    const url = userId
      ? `${this.postAPI}/user/${userId}`
      : `${this.postAPI}/mine`;

    this.http.get<any[]>(url).subscribe({
      next: posts => {
        console.log("Fetched posts:", posts);
        this.posts$.next(posts);
        this.isLoading = false;
      },
      error: err => {
        console.error('Failed to load posts:', err);
        this.isLoading = false;
      }
    });
  }

  // Comments
  openComments(post: any) {
    this.selectedPost = post;
    this.fetchComments();

    const modal = new bootstrap.Modal(
      document.getElementById('commentsModal')
    );
    modal.show();
  }

  fetchComments() {
    this.http.get<any[]>(`${this.commentAPI}/all`, {
      params: {
        postId: this.selectedPost.id
      }
    }).subscribe({
      next: comments => {
        console.log('Comments:', comments);
        this.comments$.next(comments);
      },
      error: err => console.error('Failed to load comments:', err)
    });
  }

  likeComments(comment: any) {
    this.http.post<any>(`${this.commentAPI}/like`, { commentId: comment.id })
      .subscribe({
        next: res => {
          const comments = this.comments$.value.map(c =>
            c.id === comment.id
              ? { ...c, liked: res.liked, count: res.liked ? c.count + 1 : c.count - 1 }
              : c
          );
          this.comments$.next(comments);
        },
        error: err => console.error("Comment like failed:", err)
      });
  }

  submitComment() {
    if (!this.commentData.content.trim() || !this.selectedPost) return;

    this.isSubmittingComment = true;
    this.commentData.postId = this.selectedPost.id;

    this.http.post(`${this.commentAPI}/create`, this.commentData).subscribe({
      next: res => {
        this.fetchComments();
        this.commentData.content = '';
        this.commentData.postId = 0;
        this.isSubmittingComment = false;
      },
      error: err => {
        console.error("Creating comment failed:", err);
        this.commentErrorMessage$.next('Creating comment failed');
        setTimeout(() => this.commentErrorMessage$.next(null), 1000);
      }
    });
  }

  likePosts(post: any) {
    this.http.post<any>(`${this.postAPI}/like`, { postId: post.id }).subscribe({
      next: res => {
        const posts = this.posts$.value.map(p =>
          p.id === post.id
            ? { ...p, liked: res.liked, count: res.liked ? p.count + 1 : p.count - 1 }
            : p
        );
        this.posts$.next(posts);
      },
      error: err => console.error("Post like failed:", err)
    });
  }
}
