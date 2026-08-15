import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar', standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-brand"><span class="logo">🏛️</span><span class="brand-name">Smart Tender</span></div>
      <div class="nav-right">
        <span class="user-info">{{ user?.firstName }} {{ user?.lastName }}</span>
        <span class="user-badge" *ngIf="isAdmin">ADMIN</span>
        <button class="btn-logout" (click)="auth.logout()">Sign Out</button>
      </div>
    </nav>
  `,
  styles: [`.navbar{display:flex;align-items:center;justify-content:space-between;padding:0 24px;height:60px;background:#1e3a5f;color:#fff;position:sticky;top:0;z-index:100}.nav-brand{display:flex;align-items:center;gap:10px}.logo{font-size:22px}.brand-name{font-size:18px;font-weight:700;color:#fff}.nav-right{display:flex;align-items:center;gap:16px}.user-info{font-size:14px;color:#cbd5e1}.user-badge{padding:2px 8px;background:#f59e0b;color:#fff;border-radius:10px;font-size:11px;font-weight:700}.btn-logout{padding:6px 16px;background:rgba(255,255,255,.15);color:#fff;border:1px solid rgba(255,255,255,.3);border-radius:6px;cursor:pointer;font-size:13px}.btn-logout:hover{background:rgba(255,255,255,.25)}`]
})
export class NavbarComponent {
  user = this.auth.getCurrentUser();
  isAdmin = this.auth.isAdmin();
  constructor(public auth: AuthService) {}
}
