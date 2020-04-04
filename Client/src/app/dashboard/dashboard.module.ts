import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {ToolbarModule} from '../toolbar/toolbar.module';


@NgModule({
  declarations: [DashboardComponent],
    imports: [
        CommonModule,
        ToolbarModule
    ],
  exports: [DashboardComponent]
})
export class DashboardModule {
}
