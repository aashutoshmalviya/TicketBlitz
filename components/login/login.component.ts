import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  loginData: LoginRequest = { username: '', password: '' };
  isLoading = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (!this.loginData.username || !this.loginData.password) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        this.authService.setAuthData(response);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.errorMessage.set('Invalid credentials. Please try again.');
        this.isLoading.set(false);
      },
    });
  }
}
