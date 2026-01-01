import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './home.component.html'
})
export class HomeComponent {

  content: string = '';
  selectedImage!: File;

  constructor(private http: HttpClient) {}

  // 1️⃣ Capture image from input
  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
    }
  }

  // 2️⃣ Send post to backend
  onCreate() {
    const formData = new FormData();
    formData.append('content', this.content);
    formData.append('image', this.selectedImage);

    this.http.post('http://localhost:8080/api/post/create', formData)
      .subscribe({
        next: () => {
          console.log('Post created');
          this.content = '';
        },
        error: err => console.error(err)
      });
  }
}
