import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: 'dashboard/:content',
    component: DashboardComponent
  },
  {
    path: 'dashboard',
    redirectTo: 'dashboard/logs',
    pathMatch: 'full'
  },
  {
    path: '',
    redirectTo: 'dashboard/logs',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'dashboard/logs'
  }
];
