import {Component, Input, OnInit} from '@angular/core';
import {CSR} from '../model/csr';
import {Mode} from '../model/mode.enum';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates = [];
  @Input() private mode: Mode;

  constructor() {
  }

  ngOnInit() {
  }

}
