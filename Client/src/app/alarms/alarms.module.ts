import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlarmsComponent } from './alarms.component';
import {
  MatButtonModule, MatDatepickerModule,
  MatIconModule, MatInputModule,
  MatListModule,
  MatPaginatorModule, MatSelectModule, MatSnackBarModule,
  MatSortModule,
  MatTableModule,
  MatTabsModule, MatTooltipModule
} from '@angular/material';
import {FlexLayoutModule, FlexModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {RouterModule} from '@angular/router';
import { AlarmsTabsComponent } from './alarms-tabs/alarms-tabs.component';
import {routes} from '../routing/alarms-routes';
import { AlarmListComponent } from './alarm-list/alarm-list.component';



@NgModule({
  declarations: [AlarmsComponent, AlarmsTabsComponent, AlarmListComponent],
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    MatTabsModule,
    MatButtonModule,
    FlexLayoutModule,
    FlexModule,
    MatSortModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSnackBarModule,
    ToolbarModule,
    RouterModule.forChild(routes),
    MatTooltipModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatSelectModule,
    RouterModule
  ]
})
export class AlarmsModule { }
