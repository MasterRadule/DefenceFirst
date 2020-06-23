import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {AlarmsComponent} from '../alarms/alarms.component';
import {LogsComponent} from '../logs/logs.component';
import {AuthGuard} from '../guards/auth.guard';
import {CallbackComponent} from '../callback/callback.component';

export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: CallbackComponent
      },
      {
        path: 'alarms',
        //canActivate: [AuthGuard],
        loadChildren: () => import('../alarms/alarms.module').then(c => c.AlarmsModule)
      },
      {
        path: 'logs',
        //canActivate: [AuthGuard],
        component: LogsComponent
      },
      {
        path: 'certificates',
        loadChildren: () => import('../certificates/certificates.module').then(c => c.CertificatesModule)
      }
    ]
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard'
  }
];
