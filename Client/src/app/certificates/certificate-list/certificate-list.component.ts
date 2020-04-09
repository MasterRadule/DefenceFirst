import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Mode} from '../../model/mode.enum';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {PkiApiService} from "../../core/pki-api.service";
import {Certificate} from "../../model/certificate";

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates: MatTableDataSource<Certificate>;
  displayedColumns: string[] = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'ca', 'action'];
  @Input() private mode: Mode;

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;

  constructor(private pkiApiService: PkiApiService) {
  }

  ngOnInit() {
    this.pkiApiService.getCertificates().subscribe({
      next: (certificates: Certificate[]) => {
        this.certificates = new MatTableDataSource<Certificate>(certificates);
      }
    });

    this.certificates.paginator = this.paginator;
    this.certificates.sort = this.sort;
  }

}
