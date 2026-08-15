import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TenderService } from '../../core/services/tender.service';
import { TenderSearchResult, PagedResponse } from '../../core/models/tender.model';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-tender-search', standalone: true, imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="search-page">
      <div class="page-header"><h1>🔍 Search Tenders</h1></div>
      <div class="filter-card">
        <form [formGroup]="filterForm" (ngSubmit)="search(0)">
          <div class="filter-grid">
            <div class="form-group full"><label>Keyword</label><input formControlName="keyword" placeholder="Search by title, org, keywords..."></div>
            <div class="form-group"><label>Organisation</label><input formControlName="organisation" placeholder="Organisation name"></div>
            <div class="form-group"><label>Category</label><input formControlName="category" placeholder="Product category"></div>
            <div class="form-group"><label>Source</label>
              <select formControlName="sourceType">
                <option value="">All Sources</option>
                <option value="CENTRAL">Central</option>
                <option value="STATE">States</option>
                <option value="GEM">GeM</option>
              </select>
            </div>
            <div class="form-group"><label>Status</label>
              <select formControlName="tenderStatus">
                <option value="">All Status</option>
                <option value="ACTIVE">Active</option>
                <option value="CLOSING_SOON">Closing Soon</option>
                <option value="EXPIRED">Expired</option>
              </select>
            </div>
            <div class="form-group"><label>Closing From</label><input type="date" formControlName="closingDateFrom"></div>
            <div class="form-group"><label>Closing To</label><input type="date" formControlName="closingDateTo"></div>
          </div>
          <div class="filter-actions">
            <button type="submit" class="btn-primary">Search</button>
            <button type="button" class="btn-secondary" (click)="resetFilters()">Reset</button>
            <span class="total" *ngIf="result()">{{ result()!.totalElements | number }} results</span>
          </div>
        </form>
      </div>
      <div class="sort-bar" *ngIf="result()">
        <span>Sort by: </span>
        <select [(ngModel)]="sortBy" (change)="search(0)" [ngModelOptions]="{standalone:true}">
          <option value="relevanceScore">Relevance</option>
          <option value="publishedDate">Published Date</option>
          <option value="bidSubmissionClosingDate">Closing Date</option>
        </select>
      </div>
      <div class="results" *ngIf="result()">
        <div *ngFor="let t of result()!.content" class="tender-card" (click)="goToDetail(t.tenderId)">
          <div class="card-header">
            <span class="badge" [class]="t.tenderStatus?.toLowerCase()">{{ t.tenderStatus }}</span>
            <span class="source-tag">{{ t.sourceType }}</span>
          </div>
          <h3 class="card-title">{{ t.title }}</h3>
          <p class="card-org">🏢 {{ t.organisationName }}</p>
          <div class="card-meta">
            <span>📅 Published: {{ t.publishedDate | date:'dd MMM yyyy' }}</span>
            <span class="closing" [class.urgent]="t.daysUntilClosing <= 3">⏱ Closes in {{ t.daysUntilClosing }}d</span>
            <span class="score">Score: {{ (t.relevanceScore * 100).toFixed(0) }}%</span>
          </div>
          <div class="keywords" *ngIf="t.extractedKeywords?.length">
            <span *ngFor="let k of t.extractedKeywords.slice(0,5)" class="keyword">{{ k }}</span>
          </div>
        </div>
        <div *ngIf="result()!.content.length === 0" class="empty">No tenders found. Try different filters.</div>
      </div>
      <div class="pagination" *ngIf="result() && result()!.totalPages > 1">
        <button (click)="search(currentPage - 1)" [disabled]="result()!.first">← Prev</button>
        <span>Page {{ currentPage + 1 }} of {{ result()!.totalPages }}</span>
        <button (click)="search(currentPage + 1)" [disabled]="result()!.last">Next →</button>
      </div>
      <div *ngIf="loading()" class="loading">Loading...</div>
    </div>
  `,
  styles: [`.search-page{max-width:1200px}.page-header{margin-bottom:24px}.filter-card{background:#fff;border-radius:12px;padding:24px;margin-bottom:24px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.filter-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px;margin-bottom:16px}.full{grid-column:1/-1}.form-group label{display:block;font-size:13px;font-weight:500;color:#374151;margin-bottom:6px}input,select{width:100%;padding:8px 12px;border:1.5px solid #d1d5db;border-radius:8px;font-size:14px;box-sizing:border-box}.filter-actions{display:flex;gap:12px;align-items:center}.btn-primary{padding:8px 24px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600}.btn-secondary{padding:8px 24px;background:#f1f5f9;color:#374151;border:none;border-radius:8px;cursor:pointer}.total{margin-left:auto;color:#64748b;font-size:14px}.sort-bar{display:flex;align-items:center;gap:8px;margin-bottom:16px;color:#64748b;font-size:14px}.sort-bar select{width:auto;padding:6px 10px}.tender-card{background:#fff;border-radius:10px;padding:20px;margin-bottom:12px;cursor:pointer;box-shadow:0 1px 3px rgba(0,0,0,.06);transition:box-shadow .2s;border-left:4px solid #e2e8f0}.tender-card:hover{box-shadow:0 4px 12px rgba(0,0,0,.1);border-left-color:#2563eb}.card-header{display:flex;gap:8px;margin-bottom:10px}.badge{padding:3px 10px;border-radius:20px;font-size:11px;font-weight:600}.active{background:#dcfce7;color:#166534}.closing_soon{background:#fef3c7;color:#92400e}.expired{background:#fee2e2;color:#991b1b}.source-tag{padding:3px 10px;background:#eff6ff;color:#1d4ed8;border-radius:20px;font-size:11px}.card-title{margin:0 0 8px;font-size:16px;color:#1e293b;font-weight:600}.card-org{margin:0 0 12px;color:#64748b;font-size:14px}.card-meta{display:flex;gap:16px;font-size:13px;color:#64748b;margin-bottom:10px}.closing.urgent{color:#dc2626;font-weight:600}.keywords{display:flex;flex-wrap:wrap;gap:6px}.keyword{padding:2px 8px;background:#f1f5f9;border-radius:4px;font-size:11px;color:#475569}.pagination{display:flex;align-items:center;justify-content:center;gap:16px;margin-top:24px}.pagination button{padding:8px 16px;background:#fff;border:1px solid #d1d5db;border-radius:8px;cursor:pointer}.pagination button:disabled{opacity:.4;cursor:not-allowed}.loading{text-align:center;padding:40px;color:#94a3b8}.empty{text-align:center;padding:40px;color:#94a3b8}`]
})
export class TenderSearchComponent implements OnInit {
  filterForm: FormGroup;
  result = signal<PagedResponse<TenderSearchResult> | null>(null);
  loading = signal(false);
  currentPage = 0; sortBy = 'relevanceScore';
  constructor(private fb: FormBuilder, private tenderService: TenderService, private router: Router) {
    this.filterForm = this.fb.group({ keyword:'', organisation:'', category:'', sourceType:'', tenderStatus:'', closingDateFrom:'', closingDateTo:'' });
  }
  ngOnInit() { this.search(0); }
  search(page: number) {
    this.loading.set(true); this.currentPage = page;
    const req = { ...this.filterForm.value, page, size: 20, sortBy: this.sortBy, sortDirection: 'desc' };
    this.tenderService.search(req).subscribe({
      next: r => { if (r.success) this.result.set(r.data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
  resetFilters() { this.filterForm.reset(); this.search(0); }
  goToDetail(id: string) { this.router.navigate(['/tenders', id]); }
}
