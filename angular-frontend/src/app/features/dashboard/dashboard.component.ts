import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TenderService } from '../../core/services/tender.service';
import { AuthService } from '../../core/services/auth.service';
import { TenderSearchResult } from '../../core/models/tender.model';

@Component({
  selector: 'app-dashboard', standalone: true, imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard">
      <div class="page-header">
        <h1>Welcome back, {{ user?.firstName }}! 👋</h1>
        <p>Here's what's happening with tenders today</p>
      </div>
      <div class="stats-grid">
        <div class="stat-card blue"><div class="stat-icon">📋</div><div class="stat-info"><span class="stat-value">{{ totalTenders }}</span><span class="stat-label">Active Tenders</span></div></div>
        <div class="stat-card green"><div class="stat-icon">🔔</div><div class="stat-info"><span class="stat-value">{{ closingSoon }}</span><span class="stat-label">Closing Soon</span></div></div>
        <div class="stat-card amber"><div class="stat-icon">⭐</div><div class="stat-info"><span class="stat-value">{{ savedSearches }}</span><span class="stat-label">Saved Searches</span></div></div>
        <div class="stat-card red"><div class="stat-icon">✅</div><div class="stat-info"><span class="stat-value">{{ newToday }}</span><span class="stat-label">New Today</span></div></div>
      </div>
      <div class="section">
        <div class="section-header"><h2>Recent Tenders</h2><a routerLink="/tenders" class="view-all">View All →</a></div>
        <div class="tender-list">
          <div *ngFor="let t of recentTenders()" class="tender-row" [routerLink]="['/tenders', t.tenderId]">
            <div class="tender-main">
              <span class="tender-title">{{ t.title }}</span>
              <span class="tender-org">{{ t.organisationName }}</span>
            </div>
            <div class="tender-meta">
              <span class="badge" [class]="getBadgeClass(t.tenderStatus)">{{ t.tenderStatus }}</span>
              <span class="closing">{{ t.daysUntilClosing }}d left</span>
            </div>
          </div>
          <div *ngIf="recentTenders().length === 0" class="empty">No tenders found. Trigger a fetch to get started.</div>
        </div>
      </div>
    </div>
  `,
  styles: [`.dashboard{max-width:1200px}.page-header{margin-bottom:32px}.page-header h1{font-size:28px;color:#1e293b;margin:0}.page-header p{color:#64748b;margin:4px 0 0}.stats-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:16px;margin-bottom:32px}.stat-card{background:#fff;border-radius:12px;padding:20px;display:flex;align-items:center;gap:16px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.stat-icon{font-size:32px}.stat-value{display:block;font-size:28px;font-weight:700;color:#1e293b}.stat-label{display:block;font-size:13px;color:#64748b;margin-top:2px}.blue{border-left:4px solid #2563eb}.green{border-left:4px solid #059669}.amber{border-left:4px solid #d97706}.red{border-left:4px solid #dc2626}.section{background:#fff;border-radius:12px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.section-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}.section-header h2{margin:0;font-size:18px;color:#1e293b}.view-all{color:#2563eb;text-decoration:none;font-size:14px}.tender-row{display:flex;justify-content:space-between;align-items:center;padding:12px 0;border-bottom:1px solid #f1f5f9;cursor:pointer}.tender-row:hover{background:#f8fafc;margin:0 -8px;padding:12px 8px;border-radius:6px}.tender-title{display:block;font-weight:500;color:#1e293b;font-size:14px}.tender-org{display:block;color:#64748b;font-size:12px;margin-top:2px}.tender-meta{display:flex;align-items:center;gap:12px}.badge{padding:3px 10px;border-radius:20px;font-size:11px;font-weight:600}.badge.active{background:#dcfce7;color:#166534}.badge.closing_soon,.badge.CLOSING_SOON{background:#fef3c7;color:#92400e}.badge.expired,.badge.EXPIRED{background:#fee2e2;color:#991b1b}.closing{font-size:12px;color:#64748b}.empty{text-align:center;color:#94a3b8;padding:24px}`]
})
export class DashboardComponent implements OnInit {
  user = this.authService.getCurrentUser();
  recentTenders = signal<TenderSearchResult[]>([]);
  totalTenders = 0; closingSoon = 0; savedSearches = 0; newToday = 0;
  constructor(private tenderService: TenderService, private authService: AuthService) {}
  ngOnInit() {
    this.tenderService.search({ page: 0, size: 10, tenderStatus: 'ACTIVE', sortBy: 'publishedDate', sortDirection: 'desc' })
      .subscribe(r => {
        if (r.success) {
          this.recentTenders.set(r.data.content);
          this.totalTenders = r.data.totalElements;
          this.closingSoon = r.data.content.filter(t => t.tenderStatus === 'CLOSING_SOON').length;
        }
      });
  }
  getBadgeClass(status: string) { return status?.toLowerCase().replace('_','-') || 'active'; }
}
