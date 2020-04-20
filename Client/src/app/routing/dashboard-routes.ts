import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {LogsComponent} from "../logs/logs.component";
import {AlarmsComponent} from "../alarms/alarms.component";

export const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'logs'
      },
      {
        path: 'alarms',
        component: AlarmsComponent,
        outlet: 'dashboard-content'
      },
      {
        path: 'logs',
        component: LogsComponent,
        outlet: 'dashboard-content'
      },
      {
        path: 'certificates',
        loadChildren: () => import('../certificates/certificates.module').then(c => c.CertificatesModule)
      }
    ]
  }
];
