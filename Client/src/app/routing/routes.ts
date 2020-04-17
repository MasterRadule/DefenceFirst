import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {CertificateViewComponent} from '../certificates/certificates-tabs/certificate-view/certificate-view.component';

export const routes: Routes = [
  {
    path: 'dashboard/certificates/:serialNumber',
    component: CertificateViewComponent
  },
  {
    path: 'dashboard/:content',
    component: DashboardComponent,
    pathMatch: 'full'
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
