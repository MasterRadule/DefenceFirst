import {Routes} from '@angular/router';
import {CertificateListComponent} from '../certificates/certificate-list/certificate-list.component';
import {CertificatesComponent} from '../certificates/certificates.component';
import {CertificateViewComponent} from '../certificates/certificate-view/certificate-view.component';
import {AuthGuard} from '../guards/auth.guard';
import {AlarmListComponent} from '../alarms/alarm-list/alarm-list.component';
import {AlarmsComponent} from '../alarms/alarms.component';

export const routes: Routes = [
  {
    path: '',
    component: AlarmsComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'rules'
      },
      {
        path: ':tab-content',
        component: AlarmListComponent,
        //canActivate: [AuthGuard]
      }
    ]
  }
];
