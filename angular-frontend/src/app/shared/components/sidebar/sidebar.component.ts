import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar', standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <nav class="nav-menu">
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
          <span class="icon">📊</span><span>Dashboard</span>
        </a>
        <a routerLink="/tenders" routerLinkActive="active" class="nav-item">
          <span class="icon">📋</span><span>Search Tenders</span>
        </a>
        <a routerLink="/saved-searches" routerLinkActive="active" class="nav-item">
          <span class="icon">⭐</span><span>Saved Searches</span>
        </a>
        <a routerLink="/notifications" routerLinkActive="active" class="nav-item">
          <span class="icon">🔔</span><span>Notifications</span>
        </a>
        <a routerLink="/admin" routerLinkActive="active" class="nav-item" *ngIf="isAdmin">
          <span class="icon">⚙️</span><span>Admin</span>
        </a>
      </nav>
    </aside>
  `,
  styles: [`.sidebar{width:220px;background:#fff;border-right:1px solid #e2e8f0;min-height:calc(100vh - 60px);padding:16px 0}.nav-menu{display:flex;flex-direction:column}.nav-item{display:flex;align-items:center;gap:12px;padding:12px 20px;color:#475569;text-decoration:none;font-size:14px;font-weight:500;transition:all .2s;border-radius:0}.nav-item:hover{background:#f8fafc;color:#2563eb}.nav-item.active{background:#eff6ff;color:#2563eb;border-right:3px solid #2563eb}.icon{font-size:18px;width:24px;text-align:center}`]
})
export class SidebarComponent {
  isAdmin = this.auth.isAdmin();
  constructor(private auth: AuthService) {}
}
