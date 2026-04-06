import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  profileData = {
    username: '',
    email: '',
    firstName: '',
    lastName: '',
  };

  passwordData = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  isLoading = signal(false);
  isLoadingPassword = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    const user = this.authService.currentUser();
    if (user) {
      this.profileData = {
        username: user.username || '',
        email: user.email || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
      };
    }
  }

  updateProfile() {
    if (!this.profileData.username || !this.profileData.email) return;

    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    // In a real app, this would call an API
    setTimeout(() => {
      this.successMessage.set('Profile updated successfully!');
      this.isLoading.set(false);
    }, 1000);
  }

  changePassword() {
    if (
      !this.passwordData.currentPassword ||
      !this.passwordData.newPassword ||
      !this.passwordData.confirmPassword
    )
      return;

    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.errorMessage.set('New passwords do not match.');
      return;
    }

    this.isLoadingPassword.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    // In a real app, this would call an API
    setTimeout(() => {
      this.successMessage.set('Password changed successfully!');
      this.passwordData = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      };
      this.isLoadingPassword.set(false);
    }, 1000);
  }
}
