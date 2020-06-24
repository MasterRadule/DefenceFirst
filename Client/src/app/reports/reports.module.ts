import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MatButtonModule,
  MatDatepickerModule, MatExpansionModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatPaginatorModule,
  MatSelectModule,
  MatSnackBarModule,
  MatSortModule,
  MatTableModule,
  MatTabsModule,
  MatTooltipModule
} from '@angular/material';
import {FlexLayoutModule, FlexModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {ReportsComponent} from './reports.component';


@NgModule({
  declarations: [ReportsComponent],
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
    MatTooltipModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatSelectModule,
    MatExpansionModule
  ]
})
export class ReportsModule {
}
