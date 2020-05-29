import {Injectable} from '@angular/core';
import {HttpInterceptor, HttpRequest, HttpHandler, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthorizationService} from "../core/authorization.service";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(public authService: AuthorizationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${this.authService.returnAccessToken()}`
      }
    });

    return next.handle(req);
  }
}
