import {Routes} from '@angular/router';
import {CertificateListComponent} from '../certificates/certificate-list/certificate-list.component';
import {CertificatesComponent} from '../certificates/certificates.component';
import {CertificateViewComponent} from '../certificates/certificate-view/certificate-view.component';
import {AuthGuard} from '../guards/auth.guard';

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
        component: CertificateViewComponent,
        canActivate: [AuthGuard]
      },
      {
        path: ':tab-content',
        component: CertificateListComponent,
        canActivate: [AuthGuard]
      }
    ]
  }
];
