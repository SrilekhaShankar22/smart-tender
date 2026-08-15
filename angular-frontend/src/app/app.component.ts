import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { ToasterComponent } from './shared/components/toaster/toaster.component';
import { AuthService } from './core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, LoadingSpinnerComponent, ToasterComponent, CommonModule],
  template: `
    <app-loading-spinner></app-loading-spinner>
    <app-toaster></app-toaster>
    <div class="app-wrapper" *ngIf="authService.isLoggedIn(); else publicLayout">
      <app-navbar></app-navbar>
      <div class="main-layout">
        <app-sidebar></app-sidebar>
        <main class="content"><router-outlet></router-outlet></main>
      </div>
    </div>
    <ng-template #publicLayout><router-outlet></router-outlet></ng-template>
  `,
  styles: [`
    .app-wrapper { display: flex; flex-direction: column; min-height: 100vh; }
    .main-layout { display: flex; flex: 1; }
    .content { flex: 1; padding: 24px; background: #f8fafc; overflow-y: auto; }
  `]
})
export class AppComponent {
  constructor(public authService: AuthService) {}
}
