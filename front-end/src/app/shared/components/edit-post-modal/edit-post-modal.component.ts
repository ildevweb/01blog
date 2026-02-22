import { Component, EventEmitter, Input, Output, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-post-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-post-modal.component.html'
})
export class EditPostModalComponent {
  @Input() content = '';
  @Input() imagePreview: string | ArrayBuffer | null = null;
  @Input() selectedImage: File | null = null;
  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;

  @Output() contentChange = new EventEmitter<string>();
  @Output() imageSelected = new EventEmitter<Event>();
  @Output() update = new EventEmitter<void>();
  @Output() removeMedia = new EventEmitter<void>();

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  clearFileInput(): void {
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
  }
}
