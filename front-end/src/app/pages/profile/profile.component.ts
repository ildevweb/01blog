import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Post {
  text: string;
  image?: string;
  timestamp: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent {
  // User info
  username = 'John Doe';
  bio = 'Frontend developer';
  profilePhotoUrl = '../../../assets/images/avatar.jpg';
  profileBannerUrl = '../../../assets/images/banner.jpg';

  followersCount = 120;
  followingCount = 75;

  // Admin logic (converted from JS)
  isAdmin = false;

  // Posts
  posts: Post[] = [
    {
      text: 'My first post!',
      timestamp: '2h ago',
      image: 'assets/post1.jpg',
    },
    {
      text: 'Another day coding 🚀',
      timestamp: '1d ago',
    },
  ];


  isEditProfileOpen = false;

  openEditProfile() {
    this.isEditProfileOpen = true;
  }

  closeEditProfile() {
    this.isEditProfileOpen = false;
  }
}
