import { Injectable, signal } from '@angular/core';
export interface Toast { id: number; type: 'success'|'error'|'info'|'warning'; message: string; }
@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  toasts = signal<Toast[]>([]);
  show(type: Toast['type'], message: string, duration = 4000) {
    const t: Toast = { id: ++this.counter, type, message };
    this.toasts.update(ts => [...ts, t]);
    setTimeout(() => this.toasts.update(ts => ts.filter(x => x.id !== t.id)), duration);
  }
  success(msg: string) { this.show('success', msg); }
  error(msg: string) { this.show('error', msg); }
  info(msg: string) { this.show('info', msg); }
}
