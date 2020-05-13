import {Component, KeyValueDiffers, OnInit} from '@angular/core';
import {LoginData} from "../model/login-data";
import {AuthorizationService} from "../core/authorization.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private formGroup: FormGroup;

  constructor(private authorizationService: AuthorizationService, private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.formGroup = this.formBuilder.group({
      username: [null, Validators.required],
      password: [null, Validators.required]
    });
  }

  private login() {
    let loginData = {
      'username': this.formGroup.controls['username'].value,
      'password': this.formGroup.controls['password'].value
    } as LoginData;

    this.authorizationService.login(loginData);
  }

}
