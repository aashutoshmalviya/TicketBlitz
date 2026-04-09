import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
})
export class ErrorPageComponent {
  private router = inject(Router);

  title = 'Oops! Something went wrong';
  message =
    'We encountered an unexpected error. Please try again or contact support if the problem persists.';
  showRetry = true;
  showDetails = false;
  errorDetails = '';

  // Can be set by parent component or route data
  setErrorData(data: {
    title?: string;
    message?: string;
    showRetry?: boolean;
    showDetails?: boolean;
    errorDetails?: string;
  }) {
    if (data.title) this.title = data.title;
    if (data.message) this.message = data.message;
    if (data.showRetry !== undefined) this.showRetry = data.showRetry;
    if (data.showDetails !== undefined) this.showDetails = data.showDetails;
    if (data.errorDetails) this.errorDetails = data.errorDetails;
  }

  goHome() {
    this.router.navigate(['/']);
  }

  goBack() {
    window.history.back();
  }

  retry() {
    window.location.reload();
  }
}
