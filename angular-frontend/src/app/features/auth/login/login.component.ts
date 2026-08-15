import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-login', standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-header">
          <h1>🏛️ Smart Tender</h1>
          <p>Sign in to your account</p>
        </div>
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Email</label>
            <input type="email" formControlName="email" placeholder="you@example.com">
            <span class="error" *ngIf="form.get('email')?.invalid && form.get('email')?.touched">Valid email required</span>
          </div>
          <div class="form-group">
            <label>Password</label>
            <input type="password" formControlName="password" placeholder="Password">
          </div>
          <button type="submit" class="btn-primary" [disabled]="loading || form.invalid">
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
        <div class="auth-links">
          <a routerLink="/auth/forgot-password">Forgot password?</a>
          <a routerLink="/auth/signup">Create account</a>
        </div>
      </div>
    </div>
  `,
  styles: [`.auth-page{min-height:100vh;display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#1e3a5f,#2563eb)}.auth-card{background:#fff;border-radius:12px;padding:40px;width:100%;max-width:400px;box-shadow:0 20px 60px rgba(0,0,0,.2)}.auth-header{text-align:center;margin-bottom:32px}.auth-header h1{font-size:28px;color:#1e3a5f;margin-bottom:8px}.form-group{margin-bottom:20px}label{display:block;font-size:14px;font-weight:500;color:#374151;margin-bottom:6px}input{width:100%;padding:10px 14px;border:1.5px solid #d1d5db;border-radius:8px;font-size:15px;box-sizing:border-box}.btn-primary{width:100%;padding:12px;background:#2563eb;color:#fff;border:none;border-radius:8px;font-size:15px;font-weight:600;cursor:pointer}.auth-links{display:flex;justify-content:space-between;margin-top:20px}.auth-links a{color:#2563eb;font-size:14px;text-decoration:none}.error{color:#ef4444;font-size:12px}`]
})
export class LoginComponent {
  form: FormGroup; loading = false;
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private toast: ToastService) {
    this.form = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
  }
  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.login(this.form.value).subscribe({
      next: () => { this.toast.success('Welcome back!'); this.router.navigate(['/dashboard']); },
      error: (e) => { this.toast.error(e.error?.message || 'Login failed'); this.loading = false; }
    });
  }
}
