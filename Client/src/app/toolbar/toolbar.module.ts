import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ToolbarComponent} from "./toolbar.component";
import {MatToolbarModule} from "@angular/material/toolbar";
import {FlexModule} from '@angular/flex-layout';
import {MatButtonModule} from "@angular/material/button";
import {RouterModule} from "@angular/router";


@NgModule({
  declarations: [ToolbarComponent],
  imports: [
    CommonModule,
    MatToolbarModule,
    FlexModule,
    MatButtonModule,
    RouterModule
  ],
  exports: [ToolbarComponent]
})
export class ToolbarModule {
}
