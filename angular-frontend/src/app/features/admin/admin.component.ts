import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../core/services/toast.service';
import { ApiResponse } from '../../core/models/tender.model';

@Component({
  selector: 'app-admin', standalone: true, imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header"><h1>⚙️ Admin Panel</h1></div>
      <div class="admin-grid">
        <div class="admin-card">
          <h3>🔄 Tender Fetching</h3>
          <p>Manually trigger a fetch cycle from eprocure.gov.in</p>
          <button class="btn-primary" (click)="triggerFetch()" [disabled]="fetching">
            {{ fetching ? 'Fetching...' : 'Trigger Fetch' }}
          </button>
          <div class="result" *ngIf="fetchResult">{{ fetchResult }}</div>
        </div>
        <div class="admin-card">
          <h3>📊 System Status</h3>
          <div class="status-list">
            <div class="status-row"><span>Auth Service</span><span class="dot green">●</span></div>
            <div class="status-row"><span>Fetch Service</span><span class="dot green">●</span></div>
            <div class="status-row"><span>Processing Service</span><span class="dot green">●</span></div>
            <div class="status-row"><span>Search Service</span><span class="dot green">●</span></div>
            <div class="status-row"><span>Notification Service</span><span class="dot green">●</span></div>
          </div>
        </div>
        <div class="admin-card">
          <h3>🔗 Quick Links</h3>
          <div class="links">
            <a href="http://localhost:8090" target="_blank" class="link-btn">Kafka UI</a>
            <a href="http://localhost:5601" target="_blank" class="link-btn">Kibana</a>
            <a href="http://localhost:8081/swagger-ui.html" target="_blank" class="link-btn">Fetch Swagger</a>
            <a href="http://localhost:8083/swagger-ui.html" target="_blank" class="link-btn">Search Swagger</a>
            <a href="http://localhost:8084/swagger-ui.html" target="_blank" class="link-btn">Auth Swagger</a>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`.page{max-width:1000px}.page-header{margin-bottom:24px}.admin-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:20px}.admin-card{background:#fff;border-radius:12px;padding:24px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.admin-card h3{margin:0 0 8px;font-size:16px}.admin-card p{color:#64748b;font-size:14px;margin:0 0 16px}.btn-primary{padding:8px 20px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600}.btn-primary:disabled{opacity:.6;cursor:not-allowed}.result{margin-top:12px;padding:10px;background:#f0fdf4;border-radius:6px;font-size:13px;color:#166534}.status-list{display:flex;flex-direction:column;gap:10px}.status-row{display:flex;justify-content:space-between;font-size:14px;color:#374151}.dot{font-size:16px}.dot.green{color:#059669}.dot.red{color:#dc2626}.links{display:flex;flex-direction:column;gap:8px}.link-btn{display:block;padding:8px 14px;background:#eff6ff;color:#2563eb;border-radius:6px;text-decoration:none;font-size:13px;font-weight:500}.link-btn:hover{background:#dbeafe}`]
})
export class AdminComponent {
  fetching = false; fetchResult = '';
  constructor(private http: HttpClient, private toast: ToastService) {}
  triggerFetch() {
    this.fetching = true; this.fetchResult = '';
    this.http.post<ApiResponse<any>>(`${environment.apiUrls.fetch}/fetch/trigger`, {}).subscribe({
      next: r => { this.fetchResult = r.message || 'Fetch completed'; this.toast.success('Fetch triggered!'); this.fetching = false; },
      error: () => { this.toast.error('Fetch failed'); this.fetching = false; }
    });
  }
}
