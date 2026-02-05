import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

declare var bootstrap: any;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  posts$ = new BehaviorSubject<any[]>([]);
  isLoading = false;

  errorMessage$ = new BehaviorSubject<string | null>(null);
  successMessage$ = new BehaviorSubject<string | null>(null);

  content: string = '';
  selectedImage?: File;
  currentPostId?: number;

  selectedPost: any = null;
  comments$ = new BehaviorSubject<any[]>([]);
  commentErrorMessage$ = new BehaviorSubject<string | null>(null);

  users$ = new BehaviorSubject<any[]>([]);

  //comment content
  commentData = {
    content: '',
    postId: 0
  }
  isSubmittingComment: boolean = false;

  //report part
  private reportedId: number = 0;
  private reportType: string = '';

  //pagination
  private currentPostsPage = 0;
  private currentUsersPage = 0;
  private currentCommentsPage = 0;

  private readonly postAPI = 'http://localhost:8080/api/post';
  private readonly commentAPI = 'http://localhost:8080/api/comment';
  private readonly userAPI = 'http://localhost:8080/api/user';
  private readonly reportAPI = 'http://localhost:8080/api/report';

  constructor(private http: HttpClient, private router: Router) {}

  // Called every time Home is entered
  ngOnInit(): void {
    this.fetchPosts();
    this.fetchUsers();
  }

  //GET users from backend
  fetchUsers(page: number = 0, size: number = 10): void {
    this.http.get<any[]>(`${this.userAPI}/all?page=${page}&size=${size}`)
      .subscribe({
        next: users => {
          console.log("this is the whole users:", users);

          if (page === 0) {
            this.users$.next(users);
          } else {
            const current = this.users$.getValue();
            this.users$.next([...current, ...users]);
          }
        },
        error: err => {
          console.log('Failed to load users:', err);
        }
      });
  }

  //Follow system
  follow(userId: number) {

    this.http.post<any>(`${this.userAPI}/follow`, { userId: userId })
      .subscribe({
        next: res => {
          console.log("this is the follow res :", res);
          this.fetchUsers();
        },
        error: err => console.error("Follow failed:", err)
      });
  }

  // GET posts from backend
  fetchPosts(page: number = 0, size: number = 10): void {
    this.isLoading = true;

    this.http.get<any[]>(`${this.postAPI}/all?page=${page}&size=${size}`)
      .subscribe({
        next: posts => {
          console.log("Fetched posts:", posts);

          if (page === 0) {
            this.posts$.next(posts);
          } else {
            const current = this.posts$.getValue();
            this.posts$.next([...current, ...posts]);
          }

          this.isLoading = false;
        },
        error: err => {
          console.log('Failed to load posts:', err);
          this.isLoading = false;
        }
      });
  }



  //GET comments
  openComments(post: any) {
    this.selectedPost = post;
    this.fetchComments();

    const modal = new bootstrap.Modal(
      document.getElementById('commentsModal')
    );
    modal.show();
  }

  fetchComments(page: number = 0, size: number = 10) {
    this.http.get<any[]>(`${this.commentAPI}/all?page=${page}&size=${size}`, {
      params: {
        postId: this.selectedPost.id
      }
    }).subscribe({
      next: comments => {
        console.log('this is the whole comments:', comments);

        if (page === 0) {
          this.comments$.next(comments);
        } else {
          const current = this.comments$.getValue();
          this.comments$.next([...current, ...comments]);
        }
      },
      error: err => {
        console.error('Failed to load comments:', err);
      }
    });
  }

  likeComments(comment: any) {

    this.http.post<any>(`${this.commentAPI}/like`, { commentId: comment.id })
      .subscribe({
        next: res => {
          console.log("comment liked successfully:", res);
          // Replace the post in the posts array
          const comments = this.comments$.value.map(c =>
            c.id === comment.id
              ? { ...c, liked: res.liked, count: res.liked ? c.count + 1 : c.count - 1 }
              : c
          );
          this.comments$.next(comments);
        },
        error: err => {
          console.log("comment liking failed :", err);
        }
      });
  }

  //create comment
  submitComment() {
    if (!this.commentData.content.trim() || !this.selectedPost) {
      return;
    }

    this.isSubmittingComment = true;
    this.commentData.postId = this.selectedPost.id;

    
    this.http.post(`${this.commentAPI}/create`, this.commentData)
      .subscribe({
        next: res => {
          this.fetchComments();
          this.commentData.content = '';
          this.commentData.postId = 0;
          console.log("this is comment", res);
          this.isSubmittingComment = false;
        },
        error: err => {
          console.log("comment creation error:", err);
          this.commentErrorMessage$.next('Creating comment failed');
          setTimeout(() => this.commentErrorMessage$.next(null), 1000);
        }
      });
  }


  // Capture image
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
      console.log("Selected file:", this.selectedImage.name, this.selectedImage.type)
    }
  }

  // Create post
  onCreate(): void {
    if (!this.content.trim() && !this.selectedImage) {
      this.errorMessage$.next('Post cannot be empty');
      setTimeout(() => this.errorMessage$.next(null), 1000);
      return;
    }

    const formData = new FormData();
    formData.append('content', this.content);

    if (this.selectedImage) {
      formData.append('image', this.selectedImage);
    }

    this.http.post(`${this.postAPI}/create`, formData)
      .subscribe({
        next: () => {
          this.content = '';

          this.successMessage$.next('Post created successfully');
          setTimeout(() => this.successMessage$.next(null), 1000);

          // Refresh posts immediately
          this.fetchPosts();
        },
        error: () => {
          this.errorMessage$.next('Creating post failed');
          setTimeout(() => this.errorMessage$.next(null), 1000);
        }
      });
  }

  likePosts(post: any) {

    this.http.post<any>(`${this.postAPI}/like`, { postId: post.id })
      .subscribe({
        next: res => {
          console.log("post liked successfully:", res);
          // Replace the post in the posts array
          const posts = this.posts$.value.map(p =>
            p.id === post.id
              ? { ...p, liked: res.liked, count: res.liked ? p.count + 1 : p.count - 1}
              : p
          );
          this.posts$.next(posts);
        },
        error: err => {
          console.log("post liking failed :", err);
        }
      });
  }

  //delete post
  deletePost(postId: number) {
    this.http.get(`${this.postAPI}/delete/${postId}`).subscribe({
      next: () => {
        console.log("Post deleted successfully");
        this.fetchPosts();
      },
      error: err => {
        console.error('Failed to delete post:', err);
      }
    });
  }

  //open edit modal
  openEditModal(postId: number) {
    this.currentPostId = postId;

    const modalElement = document.getElementById('editPostModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }


  onUpdatePost() {
    if (!this.currentPostId) return;

    if (!this.content.trim() && !this.selectedImage) {
      this.errorMessage$.next('Post cannot be empty');
      setTimeout(() => this.errorMessage$.next(null), 1000);
      return;
    }

    const formData = new FormData();
    formData.append('content', this.content);

    if (this.selectedImage) {
      formData.append('image', this.selectedImage);
    }

    this.http.post(`${this.postAPI}/update/${this.currentPostId}`, formData)
      .subscribe({
        next: () => {
          this.content = '';

          this.successMessage$.next('Post updated successfully');
          setTimeout(() => this.successMessage$.next(null), 1000);

          // Refresh posts immediately
          this.fetchPosts();
        },
        error: () => {
          this.errorMessage$.next('Updating post failed');
          setTimeout(() => this.errorMessage$.next(null), 1000);
        }
      });

    // After update, hide modal
    const modalElement = document.getElementById('editPostModal');
    const modal = bootstrap.Modal.getInstance(modalElement);
    modal.hide();
  }

  //redirect to user profile
  goToProfile(id: number) {
    this.router.navigate(['/profile', id]);
  }

  
  // Open Report modal
  openReportModal(reportedId: number, type: string) {
    this.reportedId = reportedId;
    this.reportType = type;


    const modalEl = document.getElementById('reportUserModal');
    if (modalEl) {
      const modal = new bootstrap.Modal(modalEl);
      modal.show();
    }
  }

  //submit report
  submitReport(reasonSpam: HTMLInputElement,
    reasonHate: HTMLInputElement,
    reasonInappropriate: HTMLInputElement,
    additionalReason: HTMLInputElement) {
      
      let selectedReason = '';

      if (reasonSpam.checked) {
        selectedReason = 'Spam';
      } else if (reasonHate.checked) {
        selectedReason = 'Hate Speech';
      } else if (reasonInappropriate.checked) {
        selectedReason = 'Inappropriate Content';
      } else if (additionalReason.value.trim()) {
        selectedReason = additionalReason.value.trim();
      }

      if (!selectedReason) {
        alert('Please select a reason');
        return;
      }

      const confirmed = window.confirm('Are you sure you want to submit this report?');

      if (!confirmed) {
        return;
      }

      let reportData = {
        reportedId: this.reportedId,
        type: this.reportType,
        reason: selectedReason,
      }

      this.http.post(
        `${this.reportAPI}/report`,
        reportData
      ).subscribe({
        next: res => {
          console.log('Report success', res)
        },
        error: err => {
          console.log("this is the report error:", err)
        }
      });

      const modalElement = document.getElementById('reportUserModal');
      const modal = bootstrap.Modal.getInstance(modalElement);
      modal.hide();
  }

  //load More button for posts
  loadMore(): void {
    this.currentPostsPage++;
    this.fetchPosts(this.currentPostsPage, 10);
  }

  //load More button for users
  loadMoreUsers(): void {
    this.currentUsersPage++;
    this.fetchUsers(this.currentUsersPage, 10);
  }

  //load More button for comments
  loadMoreComments(): void {
    this.currentCommentsPage++;
    this.fetchComments(this.currentCommentsPage, 10);
  }
}
