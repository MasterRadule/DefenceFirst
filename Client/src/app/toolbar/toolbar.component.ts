import {Component, OnInit} from '@angular/core';
import {AuthorizationService} from "../core/authorization.service";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  constructor(private authorizationService: AuthorizationService) {
  }

  ngOnInit() {
  }

}
