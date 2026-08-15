import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '../models/auth.model';
import { ApiResponse } from '../models/tender.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'st_access_token';
  private readonly REFRESH_KEY = 'st_refresh_token';
  private readonly USER_KEY = 'st_user';
  private currentUserSignal = signal<UserResponse | null>(this.loadUser());

  constructor(private http: HttpClient, private router: Router) {}

  register(req: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrls.auth}/auth/register`, req)
      .pipe(tap(r => { if (r.success) this.storeAuth(r.data); }));
  }

  login(req: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrls.auth}/auth/login`, req)
      .pipe(tap(r => { if (r.success) this.storeAuth(r.data); }));
  }

  logout(): void {
    const refresh = localStorage.getItem(this.REFRESH_KEY);
    if (refresh) {
      this.http.post(`${environment.apiUrls.auth}/auth/logout`, { refreshToken: refresh }).subscribe();
    }
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean { return !!this.getToken(); }
  getToken(): string | null { return localStorage.getItem(this.TOKEN_KEY); }
  getRefreshToken(): string | null { return localStorage.getItem(this.REFRESH_KEY); }
  getCurrentUser(): UserResponse | null { return this.currentUserSignal(); }
  isAdmin(): boolean { return this.getCurrentUser()?.roles?.includes('ROLE_ADMIN') ?? false; }

  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrls.auth}/auth/refresh`,
      { refreshToken: this.getRefreshToken() })
      .pipe(tap(r => { if (r.success) this.storeAuth(r.data); }));
  }

  private storeAuth(data: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, data.accessToken);
    localStorage.setItem(this.REFRESH_KEY, data.refreshToken);
    localStorage.setItem(this.USER_KEY, JSON.stringify(data.user));
    this.currentUserSignal.set(data.user);
  }
  private loadUser(): UserResponse | null {
    const u = localStorage.getItem(this.USER_KEY);
    return u ? JSON.parse(u) : null;
  }
}
