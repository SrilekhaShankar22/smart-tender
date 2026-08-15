import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TenderService } from '../../core/services/tender.service';
import { TenderSearchResult } from '../../core/models/tender.model';

@Component({
  selector: 'app-tender-detail', standalone: true, imports: [CommonModule, RouterLink],
  template: `
    <div class="detail-page">
      <div class="breadcrumb"><a routerLink="/tenders">← Back to Search</a></div>
      <div *ngIf="tender()" class="detail-card">
        <div class="detail-header">
          <div class="badges">
            <span class="badge" [class]="tender()!.tenderStatus?.toLowerCase()">{{ tender()!.tenderStatus }}</span>
            <span class="source">{{ tender()!.sourceType }}</span>
          </div>
          <h1>{{ tender()!.title }}</h1>
          <p class="org">🏢 {{ tender()!.organisationName }}</p>
        </div>
        <div class="detail-grid">
          <div class="info-block"><label>Reference No</label><span>{{ tender()!.tenderRefNo || '—' }}</span></div>
          <div class="info-block"><label>Category</label><span>{{ tender()!.productCategory || '—' }}</span></div>
          <div class="info-block"><label>Published Date</label><span>{{ tender()!.publishedDate | date:'dd MMM yyyy HH:mm' }}</span></div>
          <div class="info-block"><label>Bid Closing Date</label><span class="closing" [class.urgent]="tender()!.daysUntilClosing <= 3">{{ tender()!.bidSubmissionClosingDate | date:'dd MMM yyyy HH:mm' }}</span></div>
          <div class="info-block"><label>Days Remaining</label><span class="days" [class.urgent]="tender()!.daysUntilClosing <= 3">{{ tender()!.daysUntilClosing }} days</span></div>
          <div class="info-block"><label>Relevance Score</label><span>{{ (tender()!.relevanceScore * 100).toFixed(0) }}%</span></div>
        </div>
        <div class="keywords-section" *ngIf="tender()!.extractedKeywords?.length">
          <label>Keywords</label>
          <div class="keywords"><span *ngFor="let k of tender()!.extractedKeywords" class="keyword">{{ k }}</span></div>
        </div>
        <div class="actions">
          <a [href]="tender()!.detailUrl" target="_blank" class="btn-primary" *ngIf="tender()!.detailUrl">View on Portal ↗</a>
        </div>
      </div>
      <div *ngIf="loading()" class="loading">Loading tender details...</div>
      <div *ngIf="!loading() && !tender()" class="empty">Tender not found.</div>
    </div>
  `,
  styles: [`.detail-page{max-width:900px}.breadcrumb{margin-bottom:20px}.breadcrumb a{color:#2563eb;text-decoration:none;font-size:14px}.detail-card{background:#fff;border-radius:12px;padding:32px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.badges{display:flex;gap:8px;margin-bottom:16px}.badge{padding:4px 12px;border-radius:20px;font-size:12px;font-weight:600}.active{background:#dcfce7;color:#166534}.closing_soon{background:#fef3c7;color:#92400e}.expired{background:#fee2e2;color:#991b1b}.source{padding:4px 12px;background:#eff6ff;color:#1d4ed8;border-radius:20px;font-size:12px}.detail-header h1{font-size:22px;color:#1e293b;margin:0 0 8px;line-height:1.4}.org{color:#64748b;font-size:15px;margin:0 0 24px}.detail-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(250px,1fr));gap:20px;margin-bottom:24px}.info-block label{display:block;font-size:12px;color:#94a3b8;margin-bottom:4px;text-transform:uppercase;letter-spacing:.05em}.info-block span{font-size:15px;color:#1e293b;font-weight:500}.closing.urgent,.days.urgent{color:#dc2626;font-weight:700}.keywords-section{margin-bottom:24px}.keywords-section label{display:block;font-size:12px;color:#94a3b8;margin-bottom:8px;text-transform:uppercase}.keywords{display:flex;flex-wrap:wrap;gap:6px}.keyword{padding:4px 10px;background:#f1f5f9;border-radius:6px;font-size:12px;color:#475569}.actions{display:flex;gap:12px}.btn-primary{padding:10px 24px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600;text-decoration:none;display:inline-block}.loading,.empty{text-align:center;padding:60px;color:#94a3b8}`]
})
export class TenderDetailComponent implements OnInit {
  tender = signal<TenderSearchResult | null>(null);
  loading = signal(true);
  constructor(private route: ActivatedRoute, private tenderService: TenderService) {}
  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.tenderService.getById(id).subscribe({
      next: r => { if (r.success) this.tender.set(r.data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}
