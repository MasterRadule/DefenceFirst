import {AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {PkiApiService} from '../../core/pki-api.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {SnackbarService} from '../../core/snackbar.service';
import {DialogData} from '../../model/dialog-data';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Certificate} from '../../model/certificate';
import datepicker from 'js-datepicker';
import * as moment from 'moment';
import {DateAdapter, MatDatepickerInputEvent} from '@angular/material';
import {Subject} from '../../model/subject';
import {CaCertificateCreation} from '../../model/caCertificateCreation';
import {CreationData} from "../../model/creationData";
import {Data} from "@angular/router";


@Component({
  selector: 'app-ca-creation-form',
  templateUrl: './certificate-creation-form.component.html',
  styleUrls: ['./certificate-creation-form.component.css']
})
export class CertificateCreationFormComponent implements OnInit {
  private form: FormGroup;
  private manual = false;
  private algorithms = ['sha1WithRSAEncryption', 'sha256WithRSAEncryption', 'sha512WithRSAEncryption', 'dsa-with-sha256',
    'ecdsa-with-sha1'];

  private maxDateEnd: Date;
  private minDateStart: Date;
  private minDateEnd: Date;
  private maxDateStart: Date;
  private pickerDisable = true;
  private CAs: Certificate[];

  constructor(private pkiApiService: PkiApiService, private snackbarService: SnackbarService,
              private formBuilder: FormBuilder, private dialogRef: MatDialogRef<CertificateCreationFormComponent>,
              @Inject(MAT_DIALOG_DATA) private data: DialogData, private dateAdapter: DateAdapter<any>) {
  }

  ngOnInit() {
    moment.locale('sr');

    this.dateAdapter.setLocale('sr');
    this.initializeForm();

    if (!this.data.ca) {
      this.getCASelectOptions();
    }


    this.minDateStart = new Date();
    this.maxDateEnd = new Date(2030, 3, 7);

    this.maxDateStart = moment(this.maxDateEnd).subtract(3, 'months').toDate();

  }
  private initializeForm() {
    this.form = this.formBuilder.group({
      subject: this.formBuilder.group({
        commonName: [this.data.subject.commonName, Validators.required],
        organization: [this.data.subject.organization, Validators.required],
        organizationalUnit: [this.data.subject.organizationalUnit, Validators.required],
        city: [this.data.subject.city, Validators.required],
        state: [this.data.subject.state, Validators.required],
        country: [this.data.subject.country, Validators.required],
        email: [this.data.subject.email, [Validators.email, Validators.required]]
      }),
      manual: this.formBuilder.group({
        startDate: [{value: '', disabled: !this.manual}, Validators.required],
        endDate: [{value: '', disabled: !this.manual }, Validators.required],
        sigAlgorithm: [{value: '', disabled: !this.manual}, Validators.required],
        altNames: [{value: '', disabled: !this.manual}, Validators.required]
      })
    });
  }

  private toggleManual() {
    if (this.manual) {
      this.manual = false;
      this.form.controls.manual.disable();
    } else {
      this.manual = true;
      this.form.controls.manual.enable();
    }
    this.form.controls.manual.reset();
  }

  private getCASelectOptions() {
    this.pkiApiService.getCACertificates().subscribe({
      next: (data: Certificate[]) => {
        this.CAs = data;
      },
      error: () => {
        this.snackbarService.displayMessage('Failed to initialize CA select');
      }
    });
  }

  // consider deleting
  private dateValidateEnd(group: FormGroup): any {
    if (group) {
      const date = moment(group.get('startDate').value).add(3, 'months').toDate();
      if (group.get('endDate').value > date) {
        return {validEndStart : true};
      }
      return null;
    }
  }

  private setMinEnd(event: MatDatepickerInputEvent<Date>) {
    this.pickerDisable = false;
    this.minDateEnd = moment(event.value).add(3, 'months').toDate();
  }

  private submitCertificate() {
    const subjectData: Subject = this.form.controls.subject.value;
    let manualData: CaCertificateCreation = null;
    if (this.manual) {
      manualData = this.form.controls.manual.value;
    }

    const certificate: CreationData = {
      certAuthData: subjectData,
      creationData: manualData
    };

    console.log(certificate);

    this.pkiApiService.createCACertificate(certificate).subscribe({
      error: (message: string) => {
        this.snackbarService.displayMessage(message);
      }
    });
  }
}
