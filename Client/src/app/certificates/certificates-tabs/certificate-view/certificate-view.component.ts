import {Component, OnInit} from '@angular/core';
import {CertificateDetails} from '../../../model/certificate-details';
import {PkiApiService} from '../../../core/pki-api.service';
import {ActivatedRoute} from '@angular/router';
import {SnackbarService} from '../../../core/snackbar.service';
import {Location} from '@angular/common';

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

  constructor(private route: ActivatedRoute, private pkiApiService: PkiApiService, private snackbarService: SnackbarService,
              private location: Location) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.serialNumber) {
        this.getCertificate(params.serialNumber);
      }
    });
  }

  private getCertificate(serialNumber: string) {
    this.pkiApiService.getCertificate(serialNumber).subscribe(
      {
        next: (result: CertificateDetails) => {
          this.certificate = result;
        },
        error: (message: string) => {
          this.snackbarService.displayMessage(message);
        }
      });
  }
}
