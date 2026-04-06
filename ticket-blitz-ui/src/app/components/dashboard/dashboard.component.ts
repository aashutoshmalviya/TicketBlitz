import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface Booking {
  id: string;
  eventName: string;
  eventDate: string;
  status: string;
  quantity: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);

  bookings = signal<Booking[]>([]);

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    // Mock data - in real app, fetch from API
    this.bookings.set([
      {
        id: '1',
        eventName: 'Summer Music Festival',
        eventDate: '2024-07-15',
        status: 'confirmed',
        quantity: 2,
      },
      {
        id: '2',
        eventName: 'Tech Conference 2024',
        eventDate: '2024-08-20',
        status: 'pending',
        quantity: 1,
      },
    ]);
  }

  upcomingBookings() {
    return this.bookings().filter((b) => new Date(b.eventDate) > new Date());
  }

  completedBookings() {
    return this.bookings().filter((b) => b.status === 'confirmed');
  }
}
