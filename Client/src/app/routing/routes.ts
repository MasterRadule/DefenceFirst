import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {CertificateListComponent} from "../certificates/certificate-list/certificate-list.component";
import {CertificatesComponent} from "../certificates/certificates.component";
import {LogsComponent} from "../logs/logs.component";

export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'logs'
      },
      {
        path: 'logs',
        component: LogsComponent,
        outlet: 'dashboard-content'
      },
      {
        path: 'certificates',
        component: CertificatesComponent,
        outlet: 'dashboard-content',
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'csrs'
          },
          {
            path: ':tab-content',
            component: CertificateListComponent,
            outlet: 'certificates-tab-content'
          }
        ]
      }
    ]
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard'
  }
];
