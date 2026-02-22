import { Component, EventEmitter, Input, Output, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-post-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-post-form.component.html'
})
export class CreatePostFormComponent {
  @Input() content = '';
  @Input() imagePreview: string | ArrayBuffer | null = null;
  @Input() selectedImage: File | null = null;
  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;

  @Output() contentChange = new EventEmitter<string>();
  @Output() imageSelected = new EventEmitter<Event>();
  @Output() create = new EventEmitter<void>();
  @Output() removeMedia = new EventEmitter<void>();

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  clearFileInput(): void {
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
  }
}
