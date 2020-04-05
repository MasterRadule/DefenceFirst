import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {CertificatesModule} from '../certificates/certificates.module';


@NgModule({
  declarations: [DashboardComponent],
    imports: [
        CommonModule,
        ToolbarModule,
        CertificatesModule
    ],
  exports: [DashboardComponent]
})
export class DashboardModule {
}
