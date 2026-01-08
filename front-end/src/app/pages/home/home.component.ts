import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  //posts: any[] = [];
  posts$ = new BehaviorSubject<any[]>([]);
  isLoading = false;

  errorMessage$ = new BehaviorSubject<string | null>(null);
  successMessage$ = new BehaviorSubject<string | null>(null);

  content: string = '';
  selectedImage?: File;

  private readonly API = 'http://localhost:8080/api/post';

  constructor(private http: HttpClient) {}

  // Called every time Home is entered
  ngOnInit(): void {
    this.fetchPosts();
  }

  // GET posts from backend
  fetchPosts(): void {
    this.isLoading = true;

    this.http.get<any[]>(`${this.API}/all`)
      .subscribe({
        next: posts => {
          console.log("this is the whole posts:", posts);
          this.posts$.next(posts);
          this.isLoading = false;
        },
        error: err => {
          console.log('Failed to load posts:', err);
          this.isLoading = false;
        }
      });
  }

  // Capture image
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
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

    this.http.post(`${this.API}/create`, formData)
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
}
