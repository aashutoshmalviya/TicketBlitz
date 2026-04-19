import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import {
  timer,
  switchMap,
  takeWhile,
  tap,
  throwError,
  catchError,
  of,
  Observable,
} from 'rxjs';
import {
  Booking,
  BookingState,
  ReserveRequest,
  ReserveResponse,
  StatusResponse,
} from '../models/ticket.model';
import { AuthService } from './auth.service';
import { SkipLoading } from '../core/interceptor/loading.interceptor';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080';

  // Signal to hold the current booking state
  bookingState = signal<BookingState>('IDLE');

  /**
   * Reserves tickets for an event and polls the status until completion.
   * This method implements the Saga polling pattern to handle asynchronous booking.
   * @param request The reservation request containing eventId and quantity
   */
  reserveTickets(request: ReserveRequest): void {
    const currentUser = this.authService.currentUser();
    if (!currentUser || !currentUser.id) {
      this.bookingState.set('UNAUTHORIZED');
      return;
    }
    request.userId = currentUser.id;
    this.bookingState.set('PROCESSING');

    this.http
      .post<ReserveResponse>(`${this.apiUrl}/api/tickets/reserve`, request)
      .pipe(
        switchMap((response) => {
          if (response.status === 'FAILED') {
            this.bookingState.set('FAILED');
            return throwError(() => new Error('Reservation Failed'));
          }
          // Polling for status
          return timer(0, 2000).pipe(
            switchMap(() =>
              this.http
                .get<StatusResponse>(
                  `${this.apiUrl}/api/tickets/status/${response.reservationId}`,
                  {
                    context: new HttpContext().set(SkipLoading, true),
                  },
                )
                .pipe(
                  catchError((err) =>
                    of({ status: 'PROCESSING' } as StatusResponse),
                  ),
                ),
            ),
            takeWhile((status) => status.status === 'PROCESSING', true), // Continue while PENDING, emit the final status
            tap((status) => {
              if (status.status === 'CONFIRMED') {
                this.bookingState.set('SUCCESS');
              } else if (status.status === 'FAILED') {
                this.bookingState.set('FAILED');
              } else if (status.status === 'CANCELLED') {
                this.bookingState.set('CANCELLED');
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
  getUserBookings(userId: string): Observable<Booking[]> {
    return this.http.get<Booking[]>(
      `${this.apiUrl}/api/tickets/user/${userId}`,
    );
  }
}
