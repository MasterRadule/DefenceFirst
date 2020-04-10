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
  displayedColumns: string[] = ['commonName', 'organization', 'organizationalUnit', 'city', 'state', 'country', 'email'];
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
        this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
        this.pkiApiService.getCertificates().subscribe(observer);
        break;
      case Mode.PENDING:
        this.displayedColumns = ['commonName', 'organization', 'organizationalUnit', 'city', 'state', 'country', 'email', 'action'];
        this.pkiApiService.getCertificateSigningRequests().subscribe(observer);
        break;
      default:
        this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
        this.pkiApiService.getRevokedCertificates().subscribe(observer);
    }
  }

  process(row) {
    switch (this.mode) {
      case Mode.ACTIVE:
        this.revoke(row);
        break;
      case Mode.PENDING:
        this.create(row);
        break;
      default:
        this.view(row);
    }
  }

  revoke(row) {
    this.pkiApiService.revokeCertificate(row.serialNumber).subscribe(data => {
      console.log(data);
    }, error => {
      console.log(error);
    });
  }

  create(row) {
    console.log(row.serialNumber);
  }

  view(row) {
    console.log();
  }

}
