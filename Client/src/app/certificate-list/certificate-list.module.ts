import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CertificateListItemComponent} from './certificate-list-item/certificate-list-item.component';
import {CertificateListComponent} from './certificate-list.component';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {ModePipe} from '../pipes/mode.pipe';


@NgModule({
  declarations: [CertificateListItemComponent, CertificateListComponent, ModePipe],
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ]
})
export class CertificateListModule {
}
