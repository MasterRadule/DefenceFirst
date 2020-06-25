import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {AuthorizationService} from '../../core/authorization.service';

@Component({
  selector: 'app-alarms-tabs',
  templateUrl: './alarms-tabs.component.html',
  styleUrls: ['./alarms-tabs.component.css']
})
export class AlarmsTabsComponent implements OnInit {

  constructor(private router: Router, private authService: AuthorizationService) { }

  ngOnInit() {
  }

}
