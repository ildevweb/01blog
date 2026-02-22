import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-followers-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './followers-modal.component.html',
  styleUrls: ['./followers-modal.component.css']
})
export class FollowersModalComponent {
  @Input() title = 'Followers';
  @Input() users: any[] = [];

  @Output() goToProfile = new EventEmitter<number>();
  @Output() follow = new EventEmitter<number>();
}
