import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlInterceptor implements HttpInterceptor {
  private readonly baseURL: string;

  constructor() {
    this.baseURL = environment.baseURL;
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.headers.get('skip'))
      return next.handle(req.clone({headers: req.headers.delete('skip')}));

    return next.handle(req.clone({url: `${this.baseURL}/${req.url}`}));
  }
}
