import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthorizationService} from "../core/authorization.service";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptor implements HttpInterceptor {

  constructor(private authorizationService: AuthorizationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req.clone({
      setHeaders: {
        Authorization: `Bearer ${this.authorizationService.accessToken}`
      }
    }));
  }
}
