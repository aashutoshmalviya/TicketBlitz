import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/events', pathMatch: 'full' },
  {
    path: 'events',
    loadComponent: () =>
      import('./component/event-catalog/event-catalog').then(
        (m) => m.EventCatalogComponent,
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./component/login/login').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./component/register/register').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./component/dashboard/dashboard').then(
        (m) => m.DashboardComponent,
      ),
  },
  {
    path: 'event/:id',
    loadComponent: () =>
      import('./component/event-detail/event-detail').then(
        (m) => m.EventDetailComponent,
      ),
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./component/profile/profile').then(
        (m) => m.ProfileComponent,
      ),
  },
  {
    path: 'booking/:id',
    loadComponent: () =>
      import('./component/booking-details/booking-details').then(
        (m) => m.BookingDetailsComponent,
      ),
  },
  {
    path: 'confirmation',
    loadComponent: () =>
      import('./component/order-confirmation/order-confirmation').then(
        (m) => m.OrderConfirmationComponent,
      ),
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./component/notifications/notifications').then(
        (m) => m.NotificationsComponent,
      ),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./component/error-page/error-page').then(
        (m) => m.ErrorPageComponent,
      ),
  },
];
