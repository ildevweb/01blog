import { Component } from '@angular/core';
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
export class HomeComponent {
  errorMessage$ = new BehaviorSubject<string | null>(null);
  successMessage$ = new BehaviorSubject<string | null>(null);
  content: string = '';
  selectedImage!: File;

  constructor(private http: HttpClient) {}

  // Capture image from input
  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
    }
  }

  // Send post to backend
  onCreate() {
    const formData = new FormData();
    formData.append('content', this.content);
    formData.append('image', this.selectedImage);

    this.http.post('http://localhost:8080/api/post/create', formData)
      .subscribe({
        next: res => {
          console.log('Post created :', res);
          this.content = '';
          this.successMessage$.next("Post created successfully");
        },
        error: _ => this.errorMessage$.next("Creating post failed")
      });
  }
}
