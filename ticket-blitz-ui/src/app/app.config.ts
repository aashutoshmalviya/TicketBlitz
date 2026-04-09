import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient, 
  withFetch,
  withInterceptors
} from '@angular/common/http';
import { routes } from './app.routes';
import { AuthInterceptor } from './auth.interceptor';

import {
  provideClientHydration,
  withEventReplay,
} from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideClientHydration(withEventReplay()),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([AuthInterceptor])),
  ],
};
