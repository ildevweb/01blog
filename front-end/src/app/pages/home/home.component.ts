import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef  } from '@angular/core';
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

  // For Create Post
  errorMessageCreate$ = new BehaviorSubject<string | null>(null);
  successMessageCreate$ = new BehaviorSubject<string | null>(null);

  // For Update Post
  errorMessageUpdate$ = new BehaviorSubject<string | null>(null);
  successMessageUpdate$ = new BehaviorSubject<string | null>(null);

  contentCreate: string = '';
  selectedImageCreate: File | null = null;
  imagePreviewCreate: string | ArrayBuffer | null = null;
  

  contentUpdate: string = '';
  selectedImageUpdate: File | null = null;
  imagePreviewUpdate = new BehaviorSubject<string | ArrayBuffer | null>(null);

  currentPostId?: number;

  @ViewChild('fileInputCreate') fileInputCreate!: ElementRef<HTMLInputElement>;
  @ViewChild('fileInputUpdate') fileInputUpdate!: ElementRef<HTMLInputElement>;

  selectedPost: any = null;
  comments$ = new BehaviorSubject<any[]>([]);
  commentErrorMessage$ = new BehaviorSubject<string | null>(null);

  users$ = new BehaviorSubject<any[]>([]);

  commentData = { content: '', postId: 0 };
  isSubmittingComment: boolean = false;

  private reportedId: number = 0;
  private reportType: string = '';

  private currentPostsPage = 0;
  private currentUsersPage = 0;
  private currentCommentsPage = 0;

  private readonly postAPI = 'http://localhost:8080/api/post';
  private readonly commentAPI = 'http://localhost:8080/api/comment';
  private readonly userAPI = 'http://localhost:8080/api/user';
  private readonly reportAPI = 'http://localhost:8080/api/report';

  constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchPosts();
    this.fetchUsers();
  }

  fetchUsers(page: number = 0, size: number = 10): void {
    this.http.get<any[]>(`${this.userAPI}/all?page=${page}&size=${size}`).subscribe({
      next: users => {
        if (page === 0) this.users$.next(users);
        else this.users$.next([...this.users$.getValue(), ...users]);
      },
      error: err => console.log('Failed to load users:', err)
    });
  }

  follow(userId: number) {
    this.http.post<any>(`${this.userAPI}/follow`, { userId }).subscribe({
      next: () => this.fetchUsers(),
      error: err => console.error("Follow failed:", err)
    });
  }

  fetchPosts(page: number = 0, size: number = 10): void {
    this.http.get<any[]>(`${this.postAPI}/all?page=${page}&size=${size}`).subscribe({
      next: posts => {
        console.log("this is the posts :", posts);
        if (page === 0) this.posts$.next(posts);
        else this.posts$.next([...this.posts$.getValue(), ...posts]);
      },
      error: () => console.log('Failed to load posts') 
    });
  }

  openComments(post: any) {
    this.selectedPost = post;
    this.fetchComments();
    new bootstrap.Modal(document.getElementById('commentsModal')).show();
  }

  fetchComments(page: number = 0, size: number = 10) {
    this.http.get<any[]>(`${this.commentAPI}/all?page=${page}&size=${size}`, { params: { postId: this.selectedPost.id } })
      .subscribe({
        next: comments => {
          if (page === 0) this.comments$.next(comments);
          else this.comments$.next([...this.comments$.getValue(), ...comments]);
        },
        error: err => console.error('Failed to load comments:', err)
      });
  }

  likeComments(comment: any) {
    this.http.post<any>(`${this.commentAPI}/like`, { commentId: comment.id }).subscribe({
      next: res => {
        const comments = this.comments$.value.map(c =>
          c.id === comment.id ? { ...c, liked: res.liked, count: res.liked ? c.count + 1 : c.count - 1 } : c
        );
        this.comments$.next(comments);
      },
      error: err => console.log("comment liking failed :", err)
    });
  }

  submitComment() {
    if (!this.commentData.content.trim() || !this.selectedPost) return;

    this.isSubmittingComment = true;
    this.commentData.postId = this.selectedPost.id;

    this.http.post(`${this.commentAPI}/create`, this.commentData).subscribe({
      next: () => { this.fetchComments(); this.commentData.content = ''; this.isSubmittingComment = false; },
      error: () => { this.commentErrorMessage$.next('Creating comment failed'); setTimeout(() => this.commentErrorMessage$.next(null), 1000); this.isSubmittingComment = false; }
    });
  }

  onImageSelected(event: Event, type: 'create' | 'update'): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = () => {
      if (type === 'create') { 
        this.selectedImageCreate = file; 
        this.imagePreviewCreate = reader.result; 
      } else { 
        this.selectedImageUpdate = file; 
        this.imagePreviewUpdate.next(reader.result); 
      }
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);
  }

  removeMedia(type: 'create' | 'update') {
    if (type === 'create') { 
      this.selectedImageCreate = null; 
      this.imagePreviewCreate = null; 
      this.fileInputCreate.nativeElement.value = ''; 
    }
    else { 
      this.selectedImageUpdate = null; 
      this.imagePreviewUpdate.next(null); 
      this.fileInputUpdate.nativeElement.value = ''; 
    }
  }

  onCreate(): void {
    if (!this.contentCreate.trim() && !this.selectedImageCreate) return;

    const formData = new FormData();
    if (this.contentCreate.trim().length <= 100) {
      formData.append('content', this.contentCreate);
    } 
    else { 
      this.errorMessageCreate$.next("Content too large"); 
      setTimeout(() => this.errorMessageCreate$.next(null), 1000); 
      return; 
    }

    const allowedExtensions = ['png', 'jpg', 'jpeg', 'gif', 'mp4'];
    if (this.selectedImageCreate) {
      const fileExtension = this.selectedImageCreate.name.split('.').pop()?.toLowerCase();
      if ((this.selectedImageCreate.type.startsWith('image/') || this.selectedImageCreate.type.startsWith('video/')) && fileExtension && allowedExtensions.includes(fileExtension))
        formData.append('image', this.selectedImageCreate);
      else { 
        this.errorMessageCreate$.next('Invalid media type'); 
        setTimeout(() => this.errorMessageCreate$.next(null), 1000); 
        this.removeMedia('create'); 
        return; 
      }
    }

    this.http.post(`${this.postAPI}/create`, formData).subscribe({
      next: (res: any) => { 
        this.contentCreate = ''; 
        this.removeMedia('create');

        if (!res.success) {
          this.errorMessageCreate$.next(res.message); 
          setTimeout(() => this.errorMessageCreate$.next(null), 1000);
          return;
        }
        this.fetchPosts(); 
      },
      error: () => { 
        this.errorMessageCreate$.next('Post creation failed'); 
        setTimeout(() => this.errorMessageCreate$.next(null), 1000); 
        this.contentCreate = ''; 
        this.removeMedia('create'); 
      }
    });
  }

  openEditModal(post: any) {
    this.currentPostId = post.id;
    this.contentUpdate = post.content || '';
    this.selectedImageUpdate = null;
    this.imagePreviewUpdate.next(post.media ? `http://localhost:8080/${post.media}` : null);
    new bootstrap.Modal(document.getElementById('editPostModal')).show();
  }

  onUpdatePost(): void {
    if (!this.currentPostId) return;
    if (!this.contentUpdate.trim() && !this.selectedImageUpdate) return;

    const formData = new FormData();
    if (this.contentUpdate.trim().length <= 100) formData.append('content', this.contentUpdate);
    else { 
      this.errorMessageUpdate$.next("Content too large"); 
      setTimeout(() => this.errorMessageUpdate$.next(null), 1000); 
      return; 
    }

    const allowedExtensions = ['png', 'jpg', 'jpeg', 'gif', 'mp4'];
    if (this.selectedImageUpdate) {
      const fileExtension = this.selectedImageUpdate.name.split('.').pop()?.toLowerCase();
      if ((this.selectedImageUpdate.type.startsWith('image/') || this.selectedImageUpdate.type.startsWith('video/')) && fileExtension && allowedExtensions.includes(fileExtension))
        formData.append('image', this.selectedImageUpdate);
      else { 
        this.errorMessageUpdate$.next('Invalid media type'); 
        setTimeout(() => this.errorMessageUpdate$.next(null), 1000); 
        this.removeMedia('update'); 
        return; 
      }
    }

    this.http.post(`${this.postAPI}/update/${this.currentPostId}`, formData).subscribe({
      next: (res: any) => { 
        this.contentUpdate = ''; 
        this.removeMedia('update');
        if (!res.success) {
          this.errorMessageUpdate$.next(res.message); 
          setTimeout(() => this.errorMessageUpdate$.next(null), 1000);
          return;
        }

        this.fetchPosts(); 
        const modal = bootstrap.Modal.getInstance(document.getElementById('editPostModal')); 
        modal.hide(); 
      },
      error: () => { 
        this.errorMessageUpdate$.next('Updating post failed'); 
        setTimeout(() => this.errorMessageUpdate$.next(null), 1000); 
      }
    });
  }

  likePosts(post: any) {
    this.http.post<any>(`${this.postAPI}/like`, { postId: post.id }).subscribe({
      next: res => {
        const posts = this.posts$.value.map(p =>
          p.id === post.id
            ? {
                ...p,
                liked: res.liked,
                count: res.liked ? p.count + 1 : p.count - 1
              }
            : p
        );

        this.posts$.next(posts);
      },
      error: err => console.log("post liking failed :", err)
    });
  }

  deletePost(postId: number) {
    const confirmed = window.confirm('Are you sure you want to delete this post?');
    if (!confirmed) {
      return;
    }

    this.http.get(`${this.postAPI}/delete/${postId}`)
    .subscribe({ 
      next: (res: any) => {
        if (!res.success) {
          alert(res.message);
          return;
        }

        this.fetchPosts()
      }, 
      error: () => console.error('Failed to delete post') 
    });
  }

  goToProfile(id: number) { this.router.navigate(['/profile', id]); }

  openReportModal(reportedId: number, type: string) {
    this.reportedId = reportedId;
    this.reportType = type;
    new bootstrap.Modal(document.getElementById('reportUserModal')).show();
  }

  submitReport(reasonSpam: HTMLInputElement, reasonHate: HTMLInputElement, reasonInappropriate: HTMLInputElement, additionalReason: HTMLInputElement) {
    let selectedReason = reasonSpam.checked ? 'Spam' : reasonHate.checked ? 'Hate Speech' : reasonInappropriate.checked ? 'Inappropriate Content' : additionalReason.value.trim();
    if (!selectedReason) { alert('Please select a reason'); return; }
    if (!confirm('Are you sure you want to submit this report?')) return;

    this.http.post(`${this.reportAPI}/report`, { reportedId: this.reportedId, type: this.reportType, reason: selectedReason }).subscribe({ next: res => console.log('Report success', res), error: err => console.log("report error:", err) });
    const modal = bootstrap.Modal.getInstance(document.getElementById('reportUserModal'));
    modal.hide();
  }

  loadMore() { this.currentPostsPage++; this.fetchPosts(this.currentPostsPage, 10); }
  loadMoreUsers() { this.currentUsersPage++; this.fetchUsers(this.currentUsersPage, 10); }
  loadMoreComments() { this.currentCommentsPage++; this.fetchComments(this.currentCommentsPage, 10); }

}
