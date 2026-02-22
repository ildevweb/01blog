import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css']
})
export class PostCardComponent {
  @Input() post: any;

  @Output() edit = new EventEmitter<any>();
  @Output() delete = new EventEmitter<number>();
  @Output() like = new EventEmitter<any>();
  @Output() comment = new EventEmitter<any>();
  @Output() report = new EventEmitter<{ id: number; type: string }>();
}
