import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'tenders',
    loadComponent: () => import('./features/tender-search/tender-search.component').then(m => m.TenderSearchComponent),
    canActivate: [authGuard]
  },
  {
    path: 'tenders/:id',
    loadComponent: () => import('./features/tender-detail/tender-detail.component').then(m => m.TenderDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: 'saved-searches',
    loadComponent: () => import('./features/saved-searches/saved-searches.component').then(m => m.SavedSearchesComponent),
    canActivate: [authGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./features/notifications/notifications.component').then(m => m.NotificationsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];
