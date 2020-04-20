import {Routes} from '@angular/router';
import {CertificateListComponent} from "../certificates/certificate-list/certificate-list.component";
import {CertificatesComponent} from "../certificates/certificates.component";

export const routes: Routes = [
  {
    path: '',
    component: CertificatesComponent,
    outlet: 'dashboard-content',
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'csrs'
      },
      {
        path: ':tab-content',
        component: CertificateListComponent,
        outlet: 'certificates-tab-content'
      }
    ]
  }
];
