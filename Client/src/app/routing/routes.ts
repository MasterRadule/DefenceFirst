import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {CaCreationFormComponent} from '../certificates/ca-creation-form/ca-creation-form.component';

export const routes: Routes = [
  {
    path: 'dashboard/:content',
    component: DashboardComponent
  },
  {
        path: 'proba',
        component: CaCreationFormComponent
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
