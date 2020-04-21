import {Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {PkiApiService} from '../../core/pki-api.service';
import {SnackbarService} from '../../core/snackbar.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {MatDialog} from "@angular/material/dialog";
import {CertificateCreationFormComponent} from "../certificate-creation-form/certificate-creation-form.component";
import {Subject} from "../../model/subject";

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates: MatTableDataSource<any>;
  displayedColumns: string[];
  private content: string;
  private observer = {
    next: (certificates: []) => {
      this.certificates = new MatTableDataSource<any>(certificates);
      this.certificates.paginator = this.paginator;
      this.certificates.sort = this.sort;
    }
  };

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;

  constructor(private activatedRoute: ActivatedRoute, private pkiApiService: PkiApiService, private router: Router,
              private snackbarService: SnackbarService, private dialog: MatDialog) {
  }

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
        this.content = params.get('tab-content');
        if (this.content === 'csrs') {
          this.displayedColumns = ['commonName', 'organization', 'organizationalUnit', 'city', 'state', 'country', 'email'];
        } else {
          this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate'];
        }
        this.getData();
      }
    );
  }

  private getData() {
    switch (this.content) {
      case 'csrs':
        this.pkiApiService.getCertificateSigningRequests().subscribe(this.observer);
        break;
      case 'active':
        this.pkiApiService.getCertificates().subscribe(this.observer);
        break;
      default:
        this.pkiApiService.getRevokedCertificates().subscribe(this.observer);
    }
  }


  private process(row) {
    switch (this.content) {
      case 'active':
        this.revoke(row);
        this.getData();
        break;
      case 'revoked':
        this.create(row);
        break;
    }
  }

  private revoke(row) {
    this.pkiApiService.revokeCertificate(row.serialNumber).subscribe({
      next: (message: string) => {
        this.snackbarService.displayMessage(message);
      },
      error: (message: string) => this.snackbarService.displayMessage(message)
    });
  }

  private create(row) {
    console.log(row.serialNumber);
  }

  private openDialog() {
    this.dialog.open(CertificateCreationFormComponent, {
      width: '70%',
      data: {subject: {} as Subject, ca: true}
    });
  }

}
