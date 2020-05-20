import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {PkiApiService} from '../../core/pki-api.service';
import {SnackbarService} from '../../core/snackbar.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {CertificateCreationFormComponent} from '../certificate-creation-form/certificate-creation-form.component';
import {Subject} from '../../model/subject';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent implements OnInit {
  private certificates: MatTableDataSource<any> = new MatTableDataSource<any>([]);
  displayedColumns: string[];
  private content: string;
  private observer = {
    next: (certificates: []) => {
      this.certificates.data = certificates;
      this.changeDetectorRef.detectChanges();
      this.certificates.paginator = this.paginator;
      this.certificates.sort = this.sort;
    },
    error: () => {
      this.snackbarService.displayMessage('Failed to load data');
    }
  };

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;
  @ViewChild(MatTable, {static: true}) table: MatTable<any>;

  constructor(private activatedRoute: ActivatedRoute, private pkiApiService: PkiApiService, private router: Router,
              private snackbarService: SnackbarService, private dialog: MatDialog,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
        this.content = params.get('tab-content');
        if (this.content === 'csrs') {
          this.displayedColumns = ['commonName', 'organization', 'organizationalUnit', 'city', 'state', 'country', 'email', 'action'];
        } else if (this.content === 'active') {
          this.displayedColumns = ['serialNumber', 'commonName', 'issuer', 'startDate', 'endDate', 'action'];
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

  private revoke(row) {
    this.pkiApiService.revokeCertificate(row.serialNumber).subscribe({
      next: (message: any) => {
        this.snackbarService.displayMessage(message);
        this.getData();
      },
      error: (message: any) => {
        this.snackbarService.displayMessage(message);
      }
    });
  }

  private rejectCSR(row) {
    this.pkiApiService.rejectCSR(row.serialNumber).subscribe({
      next: (message: any) => {
        this.snackbarService.displayMessage(message);
        this.getData();
      },
      error: (message: any) => {
        this.snackbarService.displayMessage(message);
      }
    });
  }

  private openDialog($event, subject: Subject = {} as Subject, ca: boolean = true) {
    if ($event.target.classList.contains('mat-button-wrapper')) {
      return;
    }

    const dialogRef = this.dialog.open(CertificateCreationFormComponent, {
      width: '70%',
      data: {subject, ca}
    });
    dialogRef.afterClosed().subscribe(() => {
      this.getData();
    });
  }

  redirectToCertView($event, serialNumber) {
    if (!$event.target.classList.contains('mat-button-wrapper')) {
      this.router.navigate([`/dashboard/certificates/${this.content}/${serialNumber}`]);
    }
  }

}
