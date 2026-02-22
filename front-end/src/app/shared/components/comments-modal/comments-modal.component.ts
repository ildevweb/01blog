import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-comments-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comments-modal.component.html'
})
export class CommentsModalComponent {
  @Input() comments: any[] = [];
  @Input() commentContent = '';
  @Input() commentError: string | null = null;
  @Input() isSubmitting = false;
  @Input() hasComments = false;

  @Output() commentContentChange = new EventEmitter<string>();
  @Output() submitComment = new EventEmitter<void>();
  @Output() likeComment = new EventEmitter<any>();
  @Output() loadMoreComments = new EventEmitter<void>();
}
