import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-notifications', standalone: true, imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header"><h1>🔔 Notification Preferences</h1></div>
      <div class="pref-card">
        <h3>Alert Settings</h3>
        <div class="pref-row">
          <div class="pref-info"><strong>Email Notifications</strong><p>Receive alerts via email for matching tenders</p></div>
          <label class="toggle"><input type="checkbox" [(ngModel)]="prefs.emailEnabled" [ngModelOptions]="{standalone:true}"><span class="slider"></span></label>
        </div>
        <div class="pref-row">
          <div class="pref-info"><strong>Closing Soon Alerts</strong><p>Get notified 3 days before tender closes</p></div>
          <label class="toggle"><input type="checkbox" [(ngModel)]="prefs.notifyClosingSoon" [ngModelOptions]="{standalone:true}"><span class="slider"></span></label>
        </div>
        <div class="pref-row">
          <div class="pref-info"><strong>Notification Frequency</strong><p>How often to receive digest emails</p></div>
          <select [(ngModel)]="prefs.frequency" [ngModelOptions]="{standalone:true}" class="freq-select">
            <option value="INSTANT">Instant</option>
            <option value="DAILY">Daily Digest</option>
            <option value="WEEKLY">Weekly Digest</option>
          </select>
        </div>
        <div class="pref-row">
          <div class="pref-info"><strong>Minimum Relevance Score</strong><p>Only alert for tenders above this score</p></div>
          <div class="score-input">
            <input type="range" min="0" max="100" [(ngModel)]="prefs.minScore" [ngModelOptions]="{standalone:true}">
            <span>{{ prefs.minScore }}%</span>
          </div>
        </div>
        <button class="btn-primary" (click)="save()">Save Preferences</button>
      </div>
    </div>
  `,
  styles: [`.page{max-width:700px}.page-header{margin-bottom:24px}.page-header h1{margin:0}.pref-card{background:#fff;border-radius:12px;padding:28px;box-shadow:0 1px 3px rgba(0,0,0,.06)}.pref-card h3{margin:0 0 24px;font-size:17px;color:#1e293b}.pref-row{display:flex;justify-content:space-between;align-items:center;padding:16px 0;border-bottom:1px solid #f1f5f9}.pref-row:last-of-type{border:none;padding-bottom:24px}.pref-info strong{display:block;color:#1e293b;font-size:14px}.pref-info p{margin:4px 0 0;color:#64748b;font-size:13px}.toggle{position:relative;display:inline-block;width:48px;height:26px}.toggle input{opacity:0;width:0;height:0}.slider{position:absolute;inset:0;background:#cbd5e1;border-radius:26px;cursor:pointer;transition:.3s}.toggle input:checked+.slider{background:#2563eb}.slider:before{content:"";position:absolute;width:20px;height:20px;left:3px;bottom:3px;background:#fff;border-radius:50%;transition:.3s}.toggle input:checked+.slider:before{transform:translateX(22px)}.freq-select{padding:8px 12px;border:1.5px solid #d1d5db;border-radius:8px;font-size:14px}.score-input{display:flex;align-items:center;gap:12px}.score-input span{font-weight:600;color:#2563eb;min-width:40px}.btn-primary{padding:10px 28px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600;margin-top:8px}`]
})
export class NotificationsComponent {
  prefs = { emailEnabled: true, notifyClosingSoon: true, frequency: 'DAILY', minScore: 50 };
  constructor(private toast: ToastService) {}
  save() { this.toast.success('Preferences saved!'); }
}
