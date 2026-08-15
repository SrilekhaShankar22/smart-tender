import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { SavedSearchService } from '../../core/services/saved-search.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { SavedSearchResponse } from '../../core/models/saved-search.model';

@Component({
  selector: 'app-saved-searches', standalone: true, imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>⭐ Saved Searches</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancel' : '+ New Search' }}</button>
      </div>
      <div class="form-card" *ngIf="showForm">
        <h3>{{ editId ? 'Edit' : 'Create' }} Saved Search</h3>
        <form [formGroup]="form" (ngSubmit)="save()">
          <div class="form-grid">
            <div class="form-group full"><label>Name *</label><input formControlName="name" placeholder="e.g. Civil Construction Karnataka"></div>
            <div class="form-group"><label>Keywords</label><input formControlName="keywords" placeholder="civil, construction, road"></div>
            <div class="form-group"><label>Organisation</label><input formControlName="organisation" placeholder="CPWD, NHAI..."></div>
            <div class="form-group"><label>Category</label><input formControlName="category" placeholder="Works, Goods..."></div>
            <div class="form-group"><label>Source</label>
              <select formControlName="sourceType"><option value="">All</option><option value="CENTRAL">Central</option><option value="STATE">State</option><option value="GEM">GeM</option></select>
            </div>
            <div class="form-group"><label>Alert Frequency</label>
              <select formControlName="alertFrequency"><option value="INSTANT">Instant</option><option value="DAILY">Daily</option><option value="WEEKLY">Weekly</option></select>
            </div>
          </div>
          <div class="form-check">
            <input type="checkbox" formControlName="alertEnabled" id="alertEnabled">
            <label for="alertEnabled">Enable email alerts for this search</label>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn-primary" [disabled]="form.invalid">{{ editId ? 'Update' : 'Save' }}</button>
            <button type="button" class="btn-secondary" (click)="cancelEdit()">Cancel</button>
          </div>
        </form>
      </div>
      <div class="searches-grid">
        <div *ngFor="let s of searches()" class="search-card">
          <div class="card-header">
            <h3>{{ s.name }}</h3>
            <div class="actions">
              <button class="btn-icon" (click)="edit(s)" title="Edit">✏️</button>
              <button class="btn-icon danger" (click)="delete(s.id)" title="Delete">🗑️</button>
            </div>
          </div>
          <div class="tags">
            <span class="tag" *ngIf="s.keywords">🔍 {{ s.keywords }}</span>
            <span class="tag" *ngIf="s.organisation">🏢 {{ s.organisation }}</span>
            <span class="tag" *ngIf="s.sourceType">📡 {{ s.sourceType }}</span>
            <span class="tag" *ngIf="s.category">📂 {{ s.category }}</span>
          </div>
          <div class="card-footer">
            <span class="alert-badge" [class.on]="s.alertEnabled">{{ s.alertEnabled ? '🔔 Alerts ON' : '🔕 Alerts OFF' }}</span>
            <span class="freq">{{ s.alertFrequency }}</span>
          </div>
        </div>
        <div *ngIf="searches().length === 0" class="empty">No saved searches yet. Create one to get alerts!</div>
      </div>
    </div>
  `,
  styles: [`.page{max-width:1000px}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}.page-header h1{margin:0}.btn-primary{padding:8px 20px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600}.btn-secondary{padding:8px 20px;background:#f1f5f9;color:#374151;border:none;border-radius:8px;cursor:pointer}.form-card{background:#fff;border-radius:12px;padding:24px;margin-bottom:24px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.form-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px;margin-bottom:16px}.full{grid-column:1/-1}.form-group label{display:block;font-size:13px;font-weight:500;margin-bottom:6px}input,select{width:100%;padding:8px 12px;border:1.5px solid #d1d5db;border-radius:8px;font-size:14px;box-sizing:border-box}.form-check{display:flex;align-items:center;gap:8px;margin-bottom:16px;font-size:14px}.form-actions{display:flex;gap:12px}.searches-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:16px}.search-card{background:#fff;border-radius:12px;padding:20px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.card-header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:12px}.card-header h3{margin:0;font-size:15px;color:#1e293b}.actions{display:flex;gap:4px}.btn-icon{background:none;border:none;cursor:pointer;font-size:16px;padding:4px;border-radius:4px}.btn-icon:hover{background:#f1f5f9}.btn-icon.danger:hover{background:#fee2e2}.tags{display:flex;flex-wrap:wrap;gap:6px;margin-bottom:12px}.tag{padding:3px 10px;background:#f1f5f9;border-radius:20px;font-size:12px;color:#475569}.card-footer{display:flex;justify-content:space-between;align-items:center}.alert-badge{font-size:12px;font-weight:500}.alert-badge.on{color:#059669}.freq{font-size:12px;color:#94a3b8}.empty{grid-column:1/-1;text-align:center;padding:40px;color:#94a3b8}`]
})
export class SavedSearchesComponent implements OnInit {
  searches = signal<SavedSearchResponse[]>([]);
  form: FormGroup; showForm = false; editId: number | null = null;
  private userId: number;
  constructor(private svc: SavedSearchService, private auth: AuthService, private fb: FormBuilder, private toast: ToastService) {
    this.userId = this.auth.getCurrentUser()?.id ?? 0;
    this.form = this.fb.group({ name: ['', Validators.required], keywords: [''], organisation: [''], category: [''], sourceType: [''], alertEnabled: [true], alertFrequency: ['DAILY'] });
  }
  ngOnInit() { this.load(); }
  load() { this.svc.getAll(this.userId).subscribe(r => { if (r.success) this.searches.set(r.data); }); }
  save() {
    if (this.form.invalid) return;
    const req = this.form.value;
    const obs = this.editId ? this.svc.update(this.userId, this.editId, req) : this.svc.create(this.userId, req);
    obs.subscribe({ next: () => { this.toast.success(this.editId ? 'Updated!' : 'Created!'); this.cancelEdit(); this.load(); }, error: () => this.toast.error('Failed to save') });
  }
  edit(s: SavedSearchResponse) { this.editId = s.id; this.form.patchValue(s); this.showForm = true; }
  delete(id: number) {
    if (!confirm('Delete this saved search?')) return;
    this.svc.delete(this.userId, id).subscribe({ next: () => { this.toast.success('Deleted'); this.load(); }, error: () => this.toast.error('Failed') });
  }
  cancelEdit() { this.editId = null; this.form.reset({ alertEnabled: true, alertFrequency: 'DAILY' }); this.showForm = false; }
}
