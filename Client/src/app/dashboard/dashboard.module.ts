import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {CertificatesModule} from '../certificates/certificates.module';
import {RouterModule} from '@angular/router';
import {AlarmsModule} from '../alarms/alarms.module';
import {LogsModule} from '../logs/logs.module';
import {CoreModule} from '../core/core.module';
import {ToolbarModule} from "../toolbar/toolbar.module";


@NgModule({
  declarations: [DashboardComponent],
    imports: [
        CommonModule,
        CertificatesModule,
        AlarmsModule,
        LogsModule,
        CoreModule,
        RouterModule,
        ToolbarModule
    ],
  exports: [DashboardComponent]
})
export class DashboardModule {
}
