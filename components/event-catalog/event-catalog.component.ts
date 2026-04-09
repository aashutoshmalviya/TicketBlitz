import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { Event, BookingState } from '../../models/ticket.model';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-event-catalog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-catalog.component.html',
  styleUrls: ['./event-catalog.component.scss'],
})
export class EventCatalogComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  bookingService = inject(BookingService);

  events = signal<Event[]>([]);

  ngOnInit(): void {
    this.http.get<Event[]>('/api/catalog/events').pipe(
      catchError(error => {
    console.error('API Error:', error);
    return of([]); // Return an empty array or fallback state to safely complete the observable
  })
).subscribe((events) => {
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
