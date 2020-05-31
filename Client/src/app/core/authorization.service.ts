import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";
import * as auth0 from 'auth0-js';
import {BehaviorSubject, bindNodeCallback} from "rxjs";
import {SnackbarService} from "./snackbar.service";

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  private Auth0 = new auth0.WebAuth({
    clientID: environment.auth.clientID,
    domain: environment.auth.domain,
    responseType: 'id_token token',
    redirectUri: environment.auth.redirect,
    audience: environment.auth.audience,
    scope: 'openid profile email'
  });

  private authFlag = 'isLoggedIn';

  token$ = new BehaviorSubject<string>(null);
  userProfile$ = new BehaviorSubject<any>(null);
  onAuthSuccessUrl = '/dashboard';
  onAuthFailureUrl = '/dashboard';
  logoutUrl = environment.auth.logout;

  parseHash$ = bindNodeCallback(this.Auth0.parseHash.bind(this.Auth0));
  checkSession$ = bindNodeCallback(this.Auth0.checkSession.bind(this.Auth0));

  constructor(private router: Router, private snackbarService: SnackbarService) {
  }

  login() {
    this.Auth0.authorize();
  }

  handleLoginCallback() {
    if (window.location.hash && !this.isAuthenticated) {
      this.parseHash$().subscribe(
        authResult => {
          this.localLogin(authResult);
          this.router.navigate([this.onAuthSuccessUrl]).then();
        },
        err => this.handleError(err)
      )
    }
  }

  private localLogin(authResult) {
    // Observable of token
    this.token$.next(authResult.accessToken);
    // Emit value for user data subject
    this.userProfile$.next(authResult.idTokenPayload);
    // Set flag in local storage stating this app is logged in
    localStorage.setItem(this.authFlag, JSON.stringify(true));
  }

  get isAuthenticated(): boolean {
    return JSON.parse(localStorage.getItem(this.authFlag));
  }

  renewAuth() {
    if (this.isAuthenticated) {
      this.checkSession$({}).subscribe(
        authResult => {
          this.localLogin(authResult)
        },
        err => {
          localStorage.removeItem(this.authFlag);
          this.router.navigate([this.onAuthFailureUrl]).then();
        }
      );
    }
  }

  private localLogout() {
    localStorage.setItem(this.authFlag, JSON.stringify(false));
    this.token$.next(null);
    this.userProfile$.next(null);
  }

  logout() {
    this.localLogout();
    // This does a refresh and redirects back to homepage
    // Make sure you have the logout URL in your Auth0
    // Dashboard Application settings in Allowed Logout URLs
    this.Auth0.logout({
      returnTo: this.logoutUrl,
      clientID: environment.auth.clientID
    });
  }

  private handleError(err) {
    if (err.error_description) {
      this.snackbarService.displayMessage(err.error_description);
    } else {
      this.snackbarService.displayMessage(JSON.stringify(err));
    }
  }

}

