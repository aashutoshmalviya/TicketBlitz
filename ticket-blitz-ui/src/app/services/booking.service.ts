import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { timer, switchMap, takeWhile, tap } from 'rxjs';
import {
  BookingState,
  ReserveRequest,
  ReserveResponse,
  StatusResponse,
} from '../models/ticket.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  // Signal to hold the current booking state
  bookingState = signal<BookingState>('IDLE');

  /**
   * Reserves tickets for an event and polls the status until completion.
   * This method implements the Saga polling pattern to handle asynchronous booking.
   * @param request The reservation request containing eventId and quantity
   */
  reserveTickets(request: ReserveRequest): void {
    debugger;
    const currentUser = this.authService.currentUser();
    if (!currentUser || !currentUser.id) {
      console.error('Cannot reserve tickets: No user is logged in.');
      this.bookingState.set('FAILED');
      return;
    }
    request.userId = currentUser.id;
    this.bookingState.set('PROCESSING');

    this.http
      .post<ReserveResponse>('/api/tickets/reserve', request)
      .pipe(
        switchMap((response) => {
          // Start polling the status endpoint every 2 seconds
          return timer(0, 2000).pipe(
            switchMap(() =>
              this.http.get<StatusResponse>(
                `/api/tickets/status/${response.reservationId}`,
              ),
            ),
            takeWhile((status) => status.status === 'PENDING', true), // Continue while PENDING, emit the final status
            tap((status) => {
              if (status.status === 'CONFIRMED') {
                this.bookingState.set('SUCCESS');
              } else if (status.status === 'FAILED') {
                this.bookingState.set('FAILED');
              }
            }),
          );
        }),
      )
      .subscribe({
        error: () => {
          this.bookingState.set('FAILED');
        },
      });
  }
}
