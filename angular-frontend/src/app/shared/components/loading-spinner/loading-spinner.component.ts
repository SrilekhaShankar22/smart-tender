import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading-spinner', standalone: true, imports: [CommonModule],
  template: `
    <div class="overlay" *ngIf="loading.isLoading()">
      <div class="spinner-container"><div class="spinner"></div><p>Loading...</p></div>
    </div>
  `,
  styles: [`.overlay{position:fixed;inset:0;background:rgba(0,0,0,.4);z-index:9999;display:flex;align-items:center;justify-content:center}.spinner-container{text-align:center;color:#fff}.spinner{width:48px;height:48px;border:5px solid rgba(255,255,255,.3);border-top-color:#fff;border-radius:50%;animation:spin .8s linear infinite;margin:0 auto 12px}@keyframes spin{to{transform:rotate(360deg)}}`]
})
export class LoadingSpinnerComponent {
  constructor(public loading: LoadingService) {}
}
