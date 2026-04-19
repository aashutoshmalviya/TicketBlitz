import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Added RouterModule for routerLink
import { AuthService } from '../../services/auth.service';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../models/ticket.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  bookingService = inject(BookingService);
  router = inject(Router);

  bookings = signal<Booking[]>([]);
  isLoading = signal<boolean>(true);

  upcomingBookings = computed(() => {
    return this.bookings().filter(
      (b) => b.eventDate && new Date(b.eventDate) > new Date(),
    );
  });

  completedBookings = computed(() => {
    return this.bookings().filter((b) => b.status === 'CONFIRMED');
  });

  ngOnInit() {
    const user = this.authService.currentUser();

    if (!user || !user.id) {
      this.router.navigate(['/login']);
      return;
    }

    // Fetch REAL data from the backend
    this.bookingService.getUserBookings(user.id).subscribe({
      next: (data) => {
        this.bookings.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load bookings:', err);
        // You might want to set an error state here later
        this.isLoading.set(false);
      },
    });
  }
}
