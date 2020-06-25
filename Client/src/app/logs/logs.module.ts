import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogsComponent } from './logs.component';
import {
  MatButtonModule, MatCheckboxModule, MatDatepickerModule, MatDialogModule,
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
import {RouterModule} from '@angular/router';
import {routes} from '../routing/certificates-routes';
import {MatMomentDateModule} from '@angular/material-moment-adapter';



@NgModule({
  declarations: [LogsComponent],
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
    // RouterModule.forChild(routes),
    MatTooltipModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatSelectModule
  ]
})
export class LogsModule { }
