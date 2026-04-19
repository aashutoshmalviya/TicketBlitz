export interface Event {
  id: string;
  name: string;
  date: string;
  price: number;
  status: string;
}

export interface ReserveRequest {
  userId?: string;
  eventId: string;
  quantity: number;
}

export interface ReserveResponse {
  reservationId: string;
  status: string;
}

export interface StatusResponse {
  status: 'PROCESSING' | 'CONFIRMED' | 'FAILED' | 'CANCELLED';
}

export type BookingState =
  | 'IDLE'
  | 'PROCESSING'
  | 'SUCCESS'
  | 'FAILED'
  | 'UNAUTHORIZED'
  | 'CANCELLED';

export interface Booking {
  reservationId: string;
  eventId: string;
  eventName?: string;
  eventDate?: string;
  quantity: number;
  status: 'PENDING' | 'PROCESSING' | 'CONFIRMED' | 'FAILED' | 'CANCELLED';
}
