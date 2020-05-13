import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginComponent} from './login.component';
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {FlexModule} from "@angular/flex-layout";
import {MatButtonModule} from "@angular/material/button";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";


@NgModule({
  declarations: [LoginComponent],
  imports: [
    CommonModule,
    MatInputModule,
    MatIconModule,
    FlexModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [LoginComponent]
})
export class LoginModule {
}
