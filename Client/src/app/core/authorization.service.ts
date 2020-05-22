import {Injectable} from '@angular/core';
import {LoginData} from "../model/login-data";
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";
import qs from "querystring"
import * as moment from "moment"
import {SnackbarService} from "./snackbar.service";

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  private headers: HttpHeaders = new HttpHeaders().set('skip', 'true')
    .set('Content-Type', 'application/x-www-form-urlencoded');

  constructor(private httpClient: HttpClient, private router: Router, private snackbarService: SnackbarService) {
  }

  public get accessToken() {
    return localStorage.getItem('accessToken');
  }

  public get refreshToken() {
    return localStorage.getItem('refreshToken');
  }

  public login(data: LoginData) {
    data.client_id = environment.client_id;
    data.client_secret = environment.client_secret;
    data.grant_type = environment.grant_type;

    this.httpClient.post(`http://localhost:${environment.keycloak_port}/auth/realms/${environment.realm}/protocol/openid-connect/token`,
      qs.stringify(data),
      {
        headers: this.headers
      }
    ).subscribe({
      next: (response) => {
        AuthorizationService.setSession(response);
        this.router.navigateByUrl('/dashboard').then();
      },
      error: () => {
        this.snackbarService.displayMessage('Invalid credentials');
      }
    });
  }

  private static setSession(authData) {
    const now = moment();

    localStorage.setItem('accessToken', authData.access_token);
    localStorage.setItem('expiresIn', now.add(authData.expires_in, 'seconds').toISOString());
    localStorage.setItem('refreshToken', authData.refresh_token);
    localStorage.setItem('refreshExpiresIn', now.add(authData.refresh_expires_in, 'seconds').toISOString());
  }

  public logout() {
    let refreshToken = this.refreshToken;

    let data = {
      'refresh_token': refreshToken,
      'client_id': environment.client_id,
      'client_secret': environment.client_secret
    }
    this.httpClient.post(`http://localhost:${environment.keycloak_port}/auth/realms/${environment.realm}/protocol/openid-connect/logout`,
      qs.stringify(data),
      {
        headers: this.headers
      }
    ).subscribe({
      next: () => {
        localStorage.clear();
        this.router.navigateByUrl('/login').then();
      },
      error: () => {
        this.snackbarService.displayMessage('Failed to logout');
      }
    });
  }

  private static isLoggedIn() {
    return moment() < moment(localStorage.getItem('expiresIn'));
  }
}
