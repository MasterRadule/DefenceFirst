import {Routes} from '@angular/router';
import {AlarmListComponent} from '../alarms/alarm-list/alarm-list.component';
import {AlarmsComponent} from '../alarms/alarms.component';
import {AlarmCreationComponent} from '../alarms/alarm-creation/alarm-creation.component';
import {AuthGuard} from '../guards/auth.guard';

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
        path: 'create',
        component: AlarmCreationComponent,
        canActivate: [AuthGuard]
      },
      {
        path: ':tab-content',
        component: AlarmListComponent,
        canActivate: [AuthGuard]
      }
    ]
  }
];
