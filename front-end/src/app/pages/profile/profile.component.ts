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
  //posts
  posts$ = new BehaviorSubject<any[]>([]);
  isLoading = false;

  //comments
  selectedPost: any = null;
  comments$ = new BehaviorSubject<any[]>([]);
  commentErrorMessage$ = new BehaviorSubject<string | null>(null);
  
  //profile data
  profileData$ = new BehaviorSubject<any>(null);

  //check if profile is mine
  mine$ = new BehaviorSubject<boolean | undefined>(undefined);

  //followers & followings
  followers$ = new BehaviorSubject<any[]>([]);
  followeds$ = new BehaviorSubject<any[]>([]);


  //comment content
  commentData = {
    content: '',
    postId: 0
  }
  isSubmittingComment: boolean = false;

  //post data
  content: string = '';
  selectedImage?: File;
  currentPostId?: number;

  //post update errors
  errorMessage$ = new BehaviorSubject<string | null>(null);
  successMessage$ = new BehaviorSubject<string | null>(null);

  userId?: number; // Profile owner ID

  //report part
  private reportedId: number = 0;
  private reportType: string = '';

  //pagination
  private currentPage = 0;

  private readonly postAPI = 'http://localhost:8080/api/post';
  private readonly commentAPI = 'http://localhost:8080/api/comment';
  private readonly userAPI = 'http://localhost:8080/api/user';
  private readonly reportAPI = 'http://localhost:8080/api/report';

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    // Get optional ID from route
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.userId = Number(idParam);
        this.loadUserProfile(this.userId, true);
        this.mine$.next(false);
      } else {
        this.loadMyProfile();
        this.mine$.next(true);
      }
    });
  }

  // Fetch user profile by ID
  private loadUserProfile(id: number, loadPosts = false) {
    this.http.get(`${this.userAPI}/profile/${id}`).subscribe({
      next: data => {
        console.log("User profile data:", data);
        this.profileData$.next(data);
        this.userId = id;

        if (loadPosts) {
          this.fetchPosts(this.userId);
        }
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
  follow(userId: number, load = true) {

    this.http.post<any>(`${this.userAPI}/follow`, { userId: userId })
      .subscribe({
        next: res => {
          console.log("this is the follow res :", res);
          if (load == true) {
            this.loadUserProfile(userId);
          }
          this.getFollowers();
          this.getFolloweds();
        },
        error: err => console.error("Follow failed:", err)
      });
  }

  // Fetch posts for userId or logged-in user
  fetchPosts(userId?: number, page: number = 0, size: number = 10): void {
    this.isLoading = true;

    const url = userId
      ? `${this.postAPI}/user/${userId}`
      : `${this.postAPI}/mine`;

    this.http.get<any[]>(`${url}?page=${page}&size=${size}`).subscribe({
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

  //open edit modal
  openEditModal(postId: number) {
    this.currentPostId = postId;

    const modalElement = document.getElementById('editPostModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }

  // Capture image
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
    }
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

  // Show modal
  openFollowsModal(flag: string) {
    const modalEl = document.getElementById('followersModal');
    if (modalEl) {
      const modal = new bootstrap.Modal(modalEl);
      modal.show();
      if (flag == "followers") {
        this.getFollowers();
      } else if (flag == "followeds") {
        this.getFolloweds();
      }
      
    }
  }

  //get followers
  getFollowers() {
    this.http.get<any[]>(`${this.userAPI}/followers/${this.userId}`).subscribe({
      next: (users) => {
        console.log("Followers:", users);
        this.followers$.next(users);
        this.followeds$.next([]);
      },
      error: (err) => {
        console.error("Failed to load followers:", err);
      }
    });
  }

  //get followeds
  getFolloweds() {
    this.http.get<any[]>(`${this.userAPI}/followeds/${this.userId}`).subscribe({
      next: (users) => {
        console.log("Followeds:", users);
        this.followeds$.next(users);
        this.followers$.next([]);
      },
      error: (err) => {
        console.error("Failed to load followeds:", err);
      }
    });
  }

  get activeUsers$() {
    return (this.followers$.value?.length ? this.followers$ : this.followeds$) as BehaviorSubject<any[]>;
  }

  //redirect to user profile
  goToProfile(id: number) {
    const modalEl = document.getElementById('followersModal');
    if (modalEl) {
      const modalInstance = bootstrap.Modal.getInstance(modalEl);
      if (modalInstance) modalInstance.hide();
    }

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

  //load More button
  loadMore(): void {
    this.currentPage++;

    if (this.userId) {
      this.fetchPosts(this.userId, this.currentPage, 10);
    } else {
      this.fetchPosts(this.currentPage, 10);
    }
    
  }
}
