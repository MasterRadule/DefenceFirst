import {Component, Inject, OnInit} from '@angular/core';
import {PkiApiService} from '../../core/pki-api.service';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {SnackbarService} from "../../core/snackbar.service";
import {DialogData} from "../../model/dialog-data";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";


@Component({
  selector: 'app-ca-creation-form',
  templateUrl: './certificate-creation-form.component.html',
  styleUrls: ['./certificate-creation-form.component.css']
})
export class CertificateCreationFormComponent implements OnInit {
  private form: FormGroup;
  private manual: boolean = false;
  private algorithms = ['sha1WithRSAEncryption', 'sha256WithRSAEncryption', 'sha512WithRSAEncryption', 'dsa-with-sha256',
    'ecdsa-with-sha1'];

  constructor(private pkiApiService: PkiApiService, private snackbarService: SnackbarService,
              private formBuilder: FormBuilder, private dialogRef: MatDialogRef<CertificateCreationFormComponent>,
              @Inject(MAT_DIALOG_DATA) private data: DialogData) {
  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      subject: this.formBuilder.group({
        commonName: [this.data.subject.commonName, Validators.required],
        organizationName: [this.data.subject.organization, Validators.required],
        organizationalUnit: [this.data.subject.organizationalUnit, Validators.required],
        city: [this.data.subject.city, Validators.required],
        state: [this.data.subject.state, Validators.required],
        country: [this.data.subject.country, Validators.required],
        email: [this.data.subject.email, Validators.email]
      }),
      manual: this.formBuilder.group({
        startDate: {value: '', disabled: !this.manual},
        endDate: {value: '', disabled: !this.manual},
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
    }
    this.form.controls['manual'].reset();
  }

  private chosenDate(dsds) {
    console.log(dsds);
  }
}
