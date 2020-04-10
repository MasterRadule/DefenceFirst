import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'src/app/model/subject';
import { PkiApiService } from '../../core/pki-api.service';


@Component({
  selector: 'app-ca-creation-form',
  templateUrl: './ca-creation-form.component.html',
  styleUrls: ['./ca-creation-form.component.css']
})
export class CaCreationFormComponent implements OnInit {

  caCreationForm: FormGroup;


  constructor(private fb: FormBuilder,
              private pkiApiService: PkiApiService, ) { }

  ngOnInit() {
    this.createForm();
  }

  private createForm(): void {
    this.caCreationForm = this.fb.group({
      commonName: ['', Validators.required],
      organization: ['', Validators.required],
      organizationalUnit: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      country: ['', Validators.required],
      email: ['', Validators.required],
    });
  }

  private onCreateCASubmit(): void {
    const subjectDto: Subject = {
      serialNumber: null,
      commonName: this.caCreationForm.controls.commonName.value,
      organization: this.caCreationForm.controls.organization.value,
      organizationalUnit: this.caCreationForm.controls.organizationalUnit.value,
      city: this.caCreationForm.controls.city.value,
      state: this.caCreationForm.controls.state.value,
      country: this.caCreationForm.controls.country.value,
      email: this.caCreationForm.controls.email.value
    };

    this.pkiApiService.createCACertificate(subjectDto).subscribe(data => {
      console.log(data);
    }, error => {
      console.log(error);
    });

  }
}
