import {Routes} from '@angular/router';
import {AlarmListComponent} from '../alarms/alarm-list/alarm-list.component';
import {AlarmsComponent} from '../alarms/alarms.component';
import {AlarmCreationComponent} from '../alarms/alarm-creation/alarm-creation.component';

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
        component: AlarmCreationComponent
      },
      {
        path: ':tab-content',
        component: AlarmListComponent,
        //canActivate: [AuthGuard]
      }
    ]
  }
];
