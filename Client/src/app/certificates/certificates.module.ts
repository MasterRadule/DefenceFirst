import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CertificateListComponent} from './certificate-list/certificate-list.component';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {CertificatesTabsComponent} from './certificates-tabs/certificates-tabs.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {FlexLayoutModule, FlexModule} from '@angular/flex-layout';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {CaCreationFormComponent} from './ca-creation-form/ca-creation-form.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCheckboxModule, MatInputModule, MatSnackBarModule} from '@angular/material';
import {CertificateViewComponent} from './certificate-view/certificate-view.component';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {RouterModule} from '@angular/router';
import {CertificatesComponent} from './certificates.component';
import {routes} from "../routing/certificates-routes";


@NgModule({
  declarations: [CertificateListComponent, CertificatesTabsComponent, CaCreationFormComponent, CertificateViewComponent, CertificatesComponent],
  exports: [
    CertificatesTabsComponent,
    CertificateListComponent,
    CaCreationFormComponent,
    CertificateViewComponent,
    CertificatesComponent
  ],
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
    MatCheckboxModule,
    RouterModule.forChild(routes)
  ]
})
export class CertificatesModule {
}
