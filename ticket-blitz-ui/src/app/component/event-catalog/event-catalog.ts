import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { Event } from '../../models/ticket.model';
import { BookingService } from '../../services/booking.service';
import { LoadingComponent } from '../loading/loading';
import { PopupComponent } from '../popup/popup';

@Component({
  selector: 'app-event-catalog',
  standalone: true,
  imports: [CommonModule, PopupComponent],
  templateUrl: './event-catalog.html',
  styleUrls: ['./event-catalog.scss'],
})
export class EventCatalogComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  bookingService = inject(BookingService);

  events = signal<Event[]>([]);

  ngOnInit(): void {
    this.http
      .get<Event[]>('/api/catalog/events')
      .pipe(
        catchError((error) => {
          console.error('API Error:', error);
          return of([]);
        }),
      )
      .subscribe((events) => {
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
