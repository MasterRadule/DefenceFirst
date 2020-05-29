import {Component, OnInit} from '@angular/core';
import {AuthorizationService} from "./core/authorization.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  constructor(private authService: AuthorizationService) {
  }

  ngOnInit() {
    this.authService.login();
    this.authService.handleLoginCallback();
  }


}
