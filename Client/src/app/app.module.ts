import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './routing/app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {ToolbarModule} from './toolbar/toolbar.module';
import {DashboardModule} from './dashboard/dashboard.module';
import {CertificatesModule} from './certificates/certificates.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {UrlInterceptor} from './interceptors/url-interceptor';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    ToolbarModule,
    DashboardModule,
    CertificatesModule,
    HttpClientModule
  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: UrlInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
