import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-suggested-users-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './suggested-users-sidebar.component.html'
})
export class SuggestedUsersSidebarComponent {
  @Input() users: any[] = [];

  @Output() follow = new EventEmitter<number>();
  @Output() report = new EventEmitter<{ id: number; type: string }>();
  @Output() goToProfile = new EventEmitter<number>();
  @Output() loadMoreUsers = new EventEmitter<void>();
}
