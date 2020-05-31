import {Injectable} from '@angular/core';
import {HttpInterceptor, HttpRequest, HttpHandler, HttpEvent} from '@angular/common/http';
import {EMPTY, Observable} from 'rxjs';
import {AuthorizationService} from '../core/authorization.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(public authService: AuthorizationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.authService.isAuthenticated) {
      return EMPTY;
    }
    return next.handle(req);
  }
}
