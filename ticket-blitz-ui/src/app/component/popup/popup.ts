import { Component, inject, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './popup.html',
  styleUrls: ['./popup.scss'],
})
export class PopupComponent {
  private router = inject(Router);

  status = input.required<
    'IDLE' | 'PROCESSING' | 'SUCCESS' | 'FAILED' | 'UNAUTHORIZED' | 'CANCELLED'
  >();
  closeModal = output<void>();

  goToLogin() {
    this.closeModal.emit();
    this.router.navigate(['/login']);
  }
}
