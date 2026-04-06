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
}

export interface StatusResponse {
  status: 'PENDING' | 'CONFIRMED' | 'FAILED';
}

export type BookingState = 'IDLE' | 'PROCESSING' | 'SUCCESS' | 'FAILED';
