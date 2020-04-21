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
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCheckboxModule, MatInputModule, MatSnackBarModule} from '@angular/material';
import {CertificateViewComponent} from './certificate-view/certificate-view.component';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {RouterModule} from '@angular/router';
import {CertificatesComponent} from './certificates.component';
import {routes} from '../routing/certificates-routes';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatDialogModule} from "@angular/material/dialog";
import {CertificateCreationFormComponent} from "./certificate-creation-form/certificate-creation-form.component";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatMomentDateModule} from "@angular/material-moment-adapter";
import {MatSelectModule} from "@angular/material/select";


@NgModule({
  declarations: [CertificateListComponent, CertificatesTabsComponent,
    CertificateViewComponent, CertificatesComponent, CertificateCreationFormComponent],
  exports: [
    CertificatesTabsComponent,
    CertificateListComponent,
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
    RouterModule.forChild(routes),
    MatTooltipModule,
    MatDialogModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatSelectModule
  ],
  entryComponents: [CertificateCreationFormComponent]
})
export class CertificatesModule {
}
