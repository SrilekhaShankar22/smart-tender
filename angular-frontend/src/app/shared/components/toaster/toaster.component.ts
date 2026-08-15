import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toaster', standalone: true, imports: [CommonModule],
  template: `
    <div class="toaster">
      <div *ngFor="let t of toast.toasts()" class="toast" [class]="t.type">
        <span class="icon">{{ icons[t.type] }}</span>
        <span class="msg">{{ t.message }}</span>
      </div>
    </div>
  `,
  styles: [`.toaster{position:fixed;top:72px;right:20px;z-index:9998;display:flex;flex-direction:column;gap:8px;max-width:360px}.toast{display:flex;align-items:center;gap:10px;padding:12px 16px;border-radius:8px;font-size:14px;box-shadow:0 4px 12px rgba(0,0,0,.15);animation:slideIn .3s ease;color:#fff}.toast.success{background:#059669}.toast.error{background:#dc2626}.toast.info{background:#2563eb}.toast.warning{background:#d97706}.icon{font-size:18px}@keyframes slideIn{from{transform:translateX(100%);opacity:0}to{transform:none;opacity:1}}`]
})
export class ToasterComponent {
  icons: Record<string, string> = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
  constructor(public toast: ToastService) {}
}
