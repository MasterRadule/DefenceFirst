import {AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {PkiApiService} from '../../core/pki-api.service';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {SnackbarService} from "../../core/snackbar.service";
import {DialogData} from "../../model/dialog-data";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Certificate} from "../../model/certificate";
import datepicker from 'js-datepicker';
import * as moment from 'moment';


@Component({
  selector: 'app-ca-creation-form',
  templateUrl: './certificate-creation-form.component.html',
  styleUrls: ['./certificate-creation-form.component.css']
})
export class CertificateCreationFormComponent implements OnInit, AfterViewInit {
  private form: FormGroup;
  private manual: boolean = false;
  private algorithms = ['sha1WithRSAEncryption', 'sha256WithRSAEncryption', 'sha512WithRSAEncryption', 'dsa-with-sha256',
    'ecdsa-with-sha1'];

  @ViewChild('startDate', {static: true}) private startDateRef: ElementRef;
  @ViewChild('endDate', {static: true}) private endDateRef: ElementRef;

  private startDate;
  private endDate;

  private CAs: Certificate[];

  constructor(private pkiApiService: PkiApiService, private snackbarService: SnackbarService,
              private formBuilder: FormBuilder, private dialogRef: MatDialogRef<CertificateCreationFormComponent>,
              @Inject(MAT_DIALOG_DATA) private data: DialogData) {
  }

  ngOnInit() {
    moment.locale('sr');

    this.initializeForm();

    if (!this.data.ca)
      this.getCASelectOptions();
  }

  ngAfterViewInit() {
    this.startDate = datepicker(this.startDateRef.nativeElement, {
      id: 1,
      formatter: (input, date) => {
        input.value = moment(date).format('L');
      }
    });
    this.endDate = datepicker(this.endDateRef.nativeElement, {
      id: 1,
      formatter: (input, date) => {
        input.value = moment(date).format('L');
      }
    });
  }


  private initializeForm() {
    this.form = this.formBuilder.group({
      subject: this.formBuilder.group({
        commonName: [this.data.subject.commonName, Validators.required],
        organizationName: [this.data.subject.organization, Validators.required],
        organizationalUnit: [this.data.subject.organizationalUnit, Validators.required],
        city: [this.data.subject.city, Validators.required],
        state: [this.data.subject.state, Validators.required],
        country: [this.data.subject.country, Validators.required],
        email: [this.data.subject.email, [Validators.email, Validators.required]]
      }),
      manual: this.formBuilder.group({
        algorithm: {value: '', disabled: !this.manual},
        extensions: {value: '', disabled: !this.manual}
      })
    });
  }

  private toggleManual() {
    if (this.manual) {
      this.manual = false;
      this.form.controls['manual'].disable()
    } else {
      this.manual = true;
      this.form.controls['manual'].enable()
      this.form.controls['manual'].get('endDate').disable();
    }
    this.form.controls['manual'].reset();
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

}
