import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-signup', standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-header"><h1>🏛️ Smart Tender</h1><p>Create your account</p></div>
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="row">
            <div class="form-group"><label>First Name</label><input formControlName="firstName" placeholder="John"></div>
            <div class="form-group"><label>Last Name</label><input formControlName="lastName" placeholder="Doe"></div>
          </div>
          <div class="form-group"><label>Email</label><input type="email" formControlName="email" placeholder="you@example.com"></div>
          <div class="form-group"><label>Password</label><input type="password" formControlName="password" placeholder="Min 8 characters"></div>
          <button type="submit" class="btn-primary" [disabled]="loading || form.invalid">
            {{ loading ? 'Creating account...' : 'Create Account' }}
          </button>
        </form>
        <div class="auth-links"><a routerLink="/auth/login">Already have an account? Sign in</a></div>
      </div>
    </div>
  `,
  styles: [`.auth-page{min-height:100vh;display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#1e3a5f,#2563eb)}.auth-card{background:#fff;border-radius:12px;padding:40px;width:100%;max-width:440px;box-shadow:0 20px 60px rgba(0,0,0,.2)}.row{display:grid;grid-template-columns:1fr 1fr;gap:12px}.form-group{margin-bottom:16px}label{display:block;font-size:14px;font-weight:500;margin-bottom:6px}input{width:100%;padding:10px 14px;border:1.5px solid #d1d5db;border-radius:8px;box-sizing:border-box}.btn-primary{width:100%;padding:12px;background:#2563eb;color:#fff;border:none;border-radius:8px;font-size:15px;font-weight:600;cursor:pointer}.auth-links{text-align:center;margin-top:16px}.auth-links a{color:#2563eb;font-size:14px}`]
})
export class SignupComponent {
  form: FormGroup; loading = false;
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private toast: ToastService) {
    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(50)]],
      lastName:  ['', [Validators.required, Validators.maxLength(50)]],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(8)]]
    });
  }
  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.register(this.form.value).subscribe({
      next: () => { this.toast.success('Account created! Welcome!'); this.router.navigate(['/dashboard']); },
      error: (e) => { this.toast.error(e.error?.message || 'Registration failed'); this.loading = false; }
    });
  }
}
