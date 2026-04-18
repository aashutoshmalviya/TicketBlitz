import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // 1. Attach the token if we have one
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  // 2. Pass the request to the next handler, but pipe the response to catch errors
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 3. If the backend specifically rejects our token...
      if (error.status === 401) {
        console.warn('Unauthorized request detected. Token may be expired.');

        // Wipe the bad token from storage so we don't keep sending it
        authService.logout(); // Make sure you have a method like this that clears storage!

        // Redirect the user back to the login page to start fresh
        router.navigate(['/login']);
      }

      // Pass the error further down so components can still see it if needed
      return throwError(() => error);
    }),
  );
};
