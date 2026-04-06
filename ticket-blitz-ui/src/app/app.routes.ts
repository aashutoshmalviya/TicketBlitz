import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/events', pathMatch: 'full' },
  {
    path: 'events',
    loadComponent: () =>
      import('./components/event-catalog/event-catalog.component').then(
        (m) => m.EventCatalogComponent,
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(
        (m) => m.LoginComponent,
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent,
      ),
  },
  {
    path: 'event/:id',
    loadComponent: () =>
      import('./components/event-detail/event-detail.component').then(
        (m) => m.EventDetailComponent,
      ),
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./components/profile/profile.component').then(
        (m) => m.ProfileComponent,
      ),
  },
  {
    path: 'booking/:id',
    loadComponent: () =>
      import('./components/booking-details/booking-details.component').then(
        (m) => m.BookingDetailsComponent,
      ),
  },
  {
    path: 'confirmation',
    loadComponent: () =>
      import('./components/order-confirmation/order-confirmation.component').then(
        (m) => m.OrderConfirmationComponent,
      ),
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./components/notifications/notifications.component').then(
        (m) => m.NotificationsComponent,
      ),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./components/error-page/error-page.component').then(
        (m) => m.ErrorPageComponent,
      ),
  },
];
