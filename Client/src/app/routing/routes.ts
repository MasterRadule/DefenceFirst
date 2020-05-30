import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {AlarmsComponent} from '../alarms/alarms.component';
import {LogsComponent} from '../logs/logs.component';
import {AuthGuard} from "../guards/auth.guard";
import {CallbackComponent} from "../callback/callback.component";

export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    //canActivate: [AuthGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'logs'
      },
      {
        path: 'alarms',
        component: AlarmsComponent,
      },
      {
        path: 'logs',
        component: LogsComponent
      },
      {
        path: 'certificates',
        loadChildren: () => import('../certificates/certificates.module').then(c => c.CertificatesModule)
      }
    ]
  },
  {
    path: 'callback',
    component: CallbackComponent
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard'
  }
];
