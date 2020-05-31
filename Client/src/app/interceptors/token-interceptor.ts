import {Injectable} from '@angular/core';
import {HttpInterceptor, HttpRequest, HttpHandler, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthorizationService} from '../core/authorization.service';
import {filter, mergeMap} from 'rxjs/operators';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(public authService: AuthorizationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.token$
      .pipe(
        filter(token => typeof token === 'string'),
        mergeMap(token => {
          const tokenReq = req.clone({
            setHeaders: {Authorization: `Bearer ${token}`}
          });
          return next.handle(tokenReq);
        })
      );
  }
}
