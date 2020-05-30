import {Component, OnInit} from '@angular/core';
import {AuthorizationService} from "../core/authorization.service";

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private authservice: AuthorizationService) {
  }

  ngOnInit() {
    this.authservice.handleLoginCallback();
  }

}
