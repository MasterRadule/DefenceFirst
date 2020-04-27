import {AfterViewInit, Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PkiApiService} from '../../core/pki-api.service';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {SnackbarService} from "../../core/snackbar.service";
import {DialogData} from "../../model/dialog-data";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Certificate} from "../../model/certificate";
import datepicker from 'js-datepicker';
import * as moment from 'moment';
import {CaCertificateCreation} from "../../model/caCertificateCreation";
import {Subject} from '../../model/subject';


@Component({
  selector: 'app-ca-creation-form',
  templateUrl: './certificate-creation-form.component.html',
  styleUrls: ['./certificate-creation-form.component.css']
})
export class CertificateCreationFormComponent implements OnInit, AfterViewInit, OnDestroy{
  private form: FormGroup;
  private manual: boolean = false;
  private algorithms = ['sha1WithRSAEncryption', 'sha256WithRSAEncryption', 'sha512WithRSAEncryption', 'dsa-with-sha256',
    'ecdsa-with-sha1'];

  @ViewChild('startDate', {static: true}) private startDateRef: ElementRef;
  @ViewChild('endDate', {static: true}) private endDateRef: ElementRef;

  private startDate;
  private endDate;
  private maxDate: Date = new Date(2030, 3, 7);

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

    this.endDate.setMax(new Date(2030, 3, 7));
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
        startDate: [{value: '', disabled: !this.manual}, Validators.required],
        endDate: [{value: '', disabled: !this.manual }, Validators.required],
        algorithm: [{value: '', disabled: !this.manual}, [Validators.required]],
        extensions: [{value: '', disabled: !this.manual}, [Validators.required]]
      })
    });
  }

  private toggleManual() {
    if (this.manual) {
      this.manual = false;
      this.form.controls['manual'].disable();
    } else {
      this.manual = true;
      this.form.controls['manual'].enable();
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

  ngOnDestroy(): void {
    this.startDate.remove();
    this.endDate.remove();
  }

  private submitCertificate() {
    const subject: Subject = this.form.controls.subject.value;
    let a: CaCertificateCreation = null;
    if (this.manual) {
      a = this.form.controls.manual.value;
    }
    console.log(subject);
    console.log(a);
  }







}
