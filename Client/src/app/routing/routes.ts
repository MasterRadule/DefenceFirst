import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {CertificateViewComponent} from '../certificates/certificate-view/certificate-view.component';

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
    path: 'certificate/:serialNumber',
    component: CertificateViewComponent
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
