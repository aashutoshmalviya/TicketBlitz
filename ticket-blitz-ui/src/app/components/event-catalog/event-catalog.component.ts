import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Event, BookingState } from '../../models/ticket.model';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-event-catalog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-catalog.component.html',
  styleUrls: ['./event-catalog.component.css'],
})
export class EventCatalogComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  bookingService = inject(BookingService);

  events = signal<Event[]>([]);

  ngOnInit(): void {
    this.http.get<Event[]>('/api/catalog/events').subscribe((events) => {
      this.events.set(events);
    });
  }

  viewEvent(eventId: string) {
    this.router.navigate(['/event', eventId]);
  }

  buyTicket(eventId: string, mouseEvent: MouseEvent): void {
    mouseEvent.stopPropagation();
    this.bookingService.reserveTickets({ eventId, quantity: 1 });
  }
}
