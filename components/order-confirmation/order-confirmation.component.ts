import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface OrderConfirmation {
  reservationId: string;
  eventName: string;
  eventDate: string;
  quantity: number;
  totalPrice: number;
  status: string;
  nextSteps: string[];
}

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.scss'],
})
export class OrderConfirmationComponent implements OnInit {
  private router = inject(Router);

  orderDetails = signal<OrderConfirmation | null>(null);

  ngOnInit() {
    // In a real app, get order details from route params or service
    this.loadMockOrderDetails();
  }

  loadMockOrderDetails() {
    const mockOrder: OrderConfirmation = {
      reservationId: 'RSV-2024-001',
      eventName: 'Summer Music Festival',
      eventDate: '2024-07-15T19:00:00Z',
      quantity: 2,
      totalPrice: 150.0,
      status: 'confirmed',
      nextSteps: [
        'Check your email for booking confirmation',
        'Download your tickets from the booking details page',
        'Present tickets at the venue entrance',
        'Arrive 30 minutes before show time',
      ],
    };

    this.orderDetails.set(mockOrder);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  viewBooking() {
    // In a real app, navigate to the specific booking
    this.router.navigate(['/dashboard']);
  }

  downloadTickets() {
    // In a real app, trigger download
    alert('Downloading tickets...');
  }

  backToEvents() {
    this.router.navigate(['/events']);
  }
}
