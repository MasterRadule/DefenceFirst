import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Mode} from '../../model/mode.enum';
import {MatTableDataSource} from '@angular/material/table';
import {CertificateTableItem} from '../../model/certificate-table-item';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates: MatTableDataSource<CertificateTableItem>;
  displayedColumns: string[] = ['subject', 'action'];
  @Input() private mode: Mode;

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;

  constructor() {
    this.certificates = new MatTableDataSource([
      {
        subject: 'RadoPaprika'
      },
      {
        subject: 'Miko Svargla'
      },
      {
        subject: 'dsdsdsddddddddddddddddddddddddddddddddddddddddddddddd',
      },
      {
        subject: 'RadoPaprika',
      },
      {
        subject: 'Miko Svargla',
      },
      {
        subject: 'dsdsdsddddddddddddddddddddddddddddddddddddddddddddddd',
      },
      {
        subject: 'RadoPaprika',
      },
      {
        subject: 'Miko Svargla',
      },
      {
        subject: 'dsdsdsddddddddddddddddddddddddddddddddddddddddddddddd',
      }
    ]);
  }

  ngOnInit() {
    this.certificates.paginator = this.paginator;
    this.certificates.sort = this.sort;
  }

}
