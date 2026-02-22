import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-header.component.html',
  styleUrls: ['./profile-header.component.css']
})
export class ProfileHeaderComponent {
  @Input() profileData: any = null;
  @Input() isMine: boolean | null | undefined = undefined;

  @Output() openFollowsModal = new EventEmitter<string>();
  @Output() follow = new EventEmitter<number>();
  @Output() reportUser = new EventEmitter<{ id: number; type: string }>();
}
