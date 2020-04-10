import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CertificateListComponent} from './certificate-list/certificate-list.component';
import {ModePipe} from '../pipes/mode.pipe';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {CertificatesTabsComponent} from './certificates-tabs/certificates-tabs.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {FlexLayoutModule, FlexModule} from '@angular/flex-layout';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';


@NgModule({
  declarations: [CertificateListComponent, ModePipe, CertificatesTabsComponent],
  exports: [
    CertificatesTabsComponent,
    CertificateListComponent
  ],
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    MatTabsModule,
    MatButtonModule,
    FlexLayoutModule,
    MatSortModule,
    MatTableModule,
    MatPaginatorModule,
  ]
})
export class CertificatesModule {
}
