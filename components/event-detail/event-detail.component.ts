import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Event } from '../../models/ticket.model';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss'],
})
export class EventDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  bookingService = inject(BookingService);

  event = signal<Event | null>(null);
  quantity = signal(1);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.http.get<Event>(`/api/catalog/events/${id}`).subscribe({
        next: (event) => this.event.set(event),
        error: () => this.router.navigate(['/events']),
      });
    }
  }

  increaseQuantity() {
    this.quantity.update((q) => q + 1);
  }

  decreaseQuantity() {
    this.quantity.update((q) => Math.max(1, q - 1));
  }

  bookTickets() {
    if (this.event()) {
      this.bookingService.reserveTickets({
        eventId: this.event()!.id,
        quantity: this.quantity(),
      });
    }
  }
}
