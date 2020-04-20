import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-certificates-tabs',
  templateUrl: './certificates-tabs.component.html',
  styleUrls: ['./certificates-tabs.component.css']
})
export class CertificatesTabsComponent implements OnInit {

  constructor(private router: Router) {
  }

  ngOnInit() {
  }
}
