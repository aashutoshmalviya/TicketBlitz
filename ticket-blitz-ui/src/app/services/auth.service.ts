import { Injectable, signal, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: string;
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080';
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';

  private platformId = inject(PLATFORM_ID);
  private http = inject(HttpClient);
  private isBrowser = isPlatformBrowser(this.platformId);

  isLoggedIn = signal<boolean>(false);
  currentUser = signal<AuthResponse['user'] | null>(null);

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/api/auth/login`,
      credentials,
    );
  }

  register(userData: RegisterRequest) {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/api/auth/register`,
      userData,
    );
  }

  setAuthData(response: AuthResponse) {
    if (this.isBrowser) {
      localStorage.setItem(this.tokenKey, response.token);
      localStorage.setItem(this.userKey, JSON.stringify(response.user));
    }
    this.isLoggedIn.set(true);
    this.currentUser.set(response.user);
  }

  logout() {
    if (this.isBrowser) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
    this.isLoggedIn.set(false);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }
}
