import {Routes} from '@angular/router';
import {CertificateListComponent} from '../certificates/certificate-list/certificate-list.component';
import {CertificatesComponent} from '../certificates/certificates.component';
import {CertificateViewComponent} from "../certificates/certificate-view/certificate-view.component";

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
        path: ':tab-content/:serial-number',
        component: CertificateViewComponent
      },
      {
        path: ':tab-content',
        component: CertificateListComponent
      }
    ]
  }
];
