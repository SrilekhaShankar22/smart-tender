import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-forgot-password', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-header"><h1>Reset Password</h1><p>Enter your email to receive a reset link</p></div>
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-group"><label>Email</label><input type="email" formControlName="email" placeholder="you@example.com"></div>
          <button type="submit" class="btn-primary" [disabled]="form.invalid">Send Reset Link</button>
        </form>
        <div class="auth-links"><a routerLink="/auth/login">Back to login</a></div>
        <div *ngIf="sent" class="success-msg">✅ Reset link sent! Check your email.</div>
      </div>
    </div>
  `,
  styles: [`.auth-page{min-height:100vh;display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#1e3a5f,#2563eb)}.auth-card{background:#fff;border-radius:12px;padding:40px;width:100%;max-width:400px}.form-group{margin-bottom:20px}label{display:block;font-size:14px;font-weight:500;margin-bottom:6px}input{width:100%;padding:10px 14px;border:1.5px solid #d1d5db;border-radius:8px;box-sizing:border-box}.btn-primary{width:100%;padding:12px;background:#2563eb;color:#fff;border:none;border-radius:8px;cursor:pointer;font-weight:600}.auth-links{text-align:center;margin-top:12px}.auth-links a{color:#2563eb;font-size:14px}.success-msg{background:#d1fae5;color:#065f46;padding:12px;border-radius:8px;margin-top:16px;text-align:center}`]
})
export class ForgotPasswordComponent {
  form; sent = false;
  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  }
  onSubmit() { this.sent = true; }
}
