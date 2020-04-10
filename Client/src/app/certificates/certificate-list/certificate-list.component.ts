import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Mode} from '../../model/mode.enum';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {PkiApiService} from '../../core/pki-api.service';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates: MatTableDataSource<any>;
  displayedColumns: string[] = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
  @Input() private mode: Mode;

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;

  constructor(private pkiApiService: PkiApiService) {
  }

  ngOnInit() {
    const observer = {
      next: (certificates: []) => {
        this.certificates = new MatTableDataSource<any>(certificates);
        this.certificates.paginator = this.paginator;
        this.certificates.sort = this.sort;
      }
    };

    switch (this.mode) {
      case Mode.ACTIVE:
        this.displayedColumns = ['serialNumber', 'commonName', 'organization', 'organizationalUnit', 'city', 'state', 'country', 'email'];
        this.pkiApiService.getCertificates().subscribe(observer);
        break;
      case Mode.PENDING:
        this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
        this.pkiApiService.getCertificateSigningRequests().subscribe(observer);
        break;
      default:
        this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
        this.pkiApiService.getRevokedCertificates().subscribe(observer);
    }
  }
}
