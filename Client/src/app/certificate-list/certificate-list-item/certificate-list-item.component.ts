import {Component, Input, OnInit} from '@angular/core';
import {Mode} from '../../model/mode.enum';

@Component({
  selector: 'app-certificate-list-item',
  templateUrl: './certificate-list-item.component.html',
  styleUrls: ['./certificate-list-item.component.css']
})
export class CertificateListItemComponent implements OnInit {
  @Input() private subject: string;
  @Input() private mode: Mode;

  constructor() {
  }

  ngOnInit() {
  }

}
