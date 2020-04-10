import {Component, OnInit} from '@angular/core';
import {Mode} from '../../model/mode.enum';
import {MatTabChangeEvent} from "@angular/material/tabs";

@Component({
  selector: 'app-certificates-tabs',
  templateUrl: './certificates-tabs.component.html',
  styleUrls: ['./certificates-tabs.component.css']
})
export class CertificatesTabsComponent implements OnInit {
  private pending = Mode.PENDING;
  private active = Mode.ACTIVE;
  private revoked = Mode.REVOKED;

  constructor() {
  }

  ngOnInit() {
  }

}
