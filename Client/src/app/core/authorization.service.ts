import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";
import * as moment from "moment"
import * as auth0 from 'auth0-js';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {
  private auth0 = new auth0.WebAuth({
    clientID: environment.auth.clientID,
    domain: environment.auth.domain,
    responseType: 'token id_token',
    redirectUri: environment.auth.redirect,
    audience: environment.auth.audience,
    scope: environment.auth.scope
  });

  constructor(private router: Router) {
    this.getAccessToken();
  }

  public login() {
    this.auth0.authorize();
  }

  public handleLoginCallback() {
    this.auth0.parseHash((err, authResult) => {
      if (authResult && authResult.accessToken) {
        window.location.hash = '';
        this.setSession(authResult, {});
        this.getUserInfo(authResult);
        this.router.navigate(['/dashboard']);
      } else if (err) {
        console.error(`Error: ${err.error}`);
      }
    });
  }

  private getAccessToken() {
    this.auth0.checkSession({}, (_err, authResult) => {
      if (authResult && authResult.accessToken) {
        this.getUserInfo(authResult);
      }
    });
  }

  public getUserInfo(authResult) {
    this.auth0.client.userInfo(authResult.accessToken, (_err, profile) => {
      if (profile) {
        this.setSession(authResult, profile);
      }
    });
  }

  private setSession(authResult, profile) {
    localStorage.setItem('expiresAt', JSON.stringify(moment().add(authResult.expiresIn, 'millisecond')));
    localStorage.setItem('accessToken', authResult.accessToken);
    localStorage.setItem('userProfile', JSON.stringify(profile));
    localStorage.setItem('scopes', JSON.stringify(authResult.scope.split(' ')));
  }

  public logout() {
    localStorage.clear();

    this.auth0.logout({
      returnTo: 'https://localhost:4200',
      clientID: environment.auth.clientID
    });
  }

  public isLoggedIn() {
    return moment().isBefore(moment(localStorage.getItem('expiresAt')));
  }

  public getUsername() {
    const user = JSON.parse(localStorage.getItem('userProfile'));

    if (user['given_name'] != undefined)
      return user['given_name'];
    else
      return user['name'];
  }

  returnAccessToken() {
    return localStorage.getItem('accessToken');
  }

  public checkScopes(neededScopes: string[]): boolean {
    let userScopes = this.returnScopes();

    for (let scope of neededScopes) {
      if (!userScopes.includes(scope))
        return false;
    }
    return true;
  }

  private returnScopes() {
    return JSON.parse(localStorage.getItem('scopes'));
  }
}

