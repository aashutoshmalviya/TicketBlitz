import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  registerData: RegisterRequest = { username: '', email: '', password: '' };
  isLoading = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (
      !this.registerData.username ||
      !this.registerData.email ||
      !this.registerData.password
    )
      return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.authService.setAuthData(response);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.errorMessage.set('Registration failed. Please try again.');
        this.isLoading.set(false);
      },
    });
  }
}
