import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

interface BookingDetails {
  id: string;
  reservationId: string;
  eventId: string;
  eventName: string;
  eventDate: string;
  venue: string;
  quantity: number;
  totalPrice: number;
  status: string;
  createdAt: string;
  paymentStatus: string;
  tickets: Ticket[];
}

interface Ticket {
  id: string;
  ticketNumber: string;
  seatNumber?: string;
  qrCode?: string;
}

@Component({
  selector: 'app-booking-details',
  standalone: true,
  imports: [CommonModule],
  template: ``,
  templateUrl: './booking-details.component.html',
  styleUrls: ['./booking-details.component.scss'],
})
export class BookingDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  booking = signal<BookingDetails | null>(null);

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    const bookingId = this.route.snapshot.paramMap.get('id');
    if (bookingId) {
      // In a real app, fetch from API
      this.loadMockBooking(bookingId);
    }
  }

  loadMockBooking(bookingId: string) {
    // Mock data - in real app, fetch from API
    const mockBooking: BookingDetails = {
      id: bookingId,
      reservationId: 'RSV-' + bookingId,
      eventId: '1',
      eventName: 'Summer Music Festival',
      eventDate: '2024-07-15T19:00:00Z',
      venue: 'Central Park Amphitheater',
      quantity: 2,
      totalPrice: 150.0,
      status: 'confirmed',
      paymentStatus: 'completed',
      createdAt: '2024-06-01T10:30:00Z',
      tickets: [
        {
          id: '1',
          ticketNumber: 'TKT-001-001',
          seatNumber: 'A-12',
          qrCode:
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==',
        },
        {
          id: '2',
          ticketNumber: 'TKT-001-002',
          seatNumber: 'A-13',
          qrCode:
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==',
        },
      ],
    };

    this.booking.set(mockBooking);
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  downloadTicket(ticket: Ticket) {
    // In a real app, this would generate/download a PDF
    alert(`Downloading ticket ${ticket.ticketNumber}`);
  }

  viewTickets() {
    // Scroll to tickets section
    document
      .querySelector('.tickets-section')
      ?.scrollIntoView({ behavior: 'smooth' });
  }

  cancelBooking() {
    if (confirm('Are you sure you want to cancel this booking?')) {
      // In a real app, call API to cancel
      alert('Booking cancelled successfully');
      this.router.navigate(['/dashboard']);
    }
  }

  retryPayment() {
    // In a real app, redirect to payment
    alert('Redirecting to payment...');
  }
}
