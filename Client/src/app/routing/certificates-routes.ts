import {Routes} from '@angular/router';
import {CertificateListComponent} from "../certificates/certificate-list/certificate-list.component";
import {CertificatesComponent} from "../certificates/certificates.component";

export const routes: Routes = [
  {
    path: '',
    component: CertificatesComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'csrs'
      },
      {
        path: ':tab-content',
        component: CertificateListComponent,
      }
    ]
  }
];
