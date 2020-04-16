import {Component, OnInit} from '@angular/core';
import {CertificateDetails} from '../../model/certificate-details';
import {PkiApiService} from '../../core/pki-api.service';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-certificate-view',
  templateUrl: './certificate-view.component.html',
  styleUrls: ['./certificate-view.component.css']
})
export class CertificateViewComponent implements OnInit {
  certificate: CertificateDetails = {
    subjectData: {
      serialNumber: '',
      commonName: '',
      organization: '',
      organizationalUnit: '',
      country: '',
      city: '',
      state: '',
      email: ''
    }, startDate: null, endDate: null, issuer: '', ca: false
  };

  constructor(private route: ActivatedRoute, private pkiApiService: PkiApiService, private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.serialNumber) {
        this.getCertificate(params.serialNumber);
      }
    });
  }

  getCertificate(serialNumber: string) {
    this.pkiApiService.getCertificate(serialNumber).subscribe(
      {
        next: (result: CertificateDetails) => {
          this.certificate = result;
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

}
