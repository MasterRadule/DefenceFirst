import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTable, MatTableDataSource} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../core/snackbar.service';
import {AuthorizationService} from '../core/authorization.service';
import {SearchLogsDTO} from '../model/search-logs-dto';
import {LogsService} from '../core/logs.service';
import * as moment from 'moment';
import {Page} from '../model/page';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {
  private logs: MatTableDataSource<any> = new MatTableDataSource<any>([]);
  displayedColumns: string[];
  private searchParameters: SearchLogsDTO;
  private activeSearchParameters: SearchLogsDTO;
  private severities: string[];
  private facilities: string[];
  private observer = {
    next: (logs: []) => {
      this.logs.data = logs;
      this.changeDetectorRef.detectChanges();
      this.logs.paginator = this.paginator;
      this.logs.sort = this.sort;
    },
    error: () => {
      this.snackbarService.displayMessage('Failed to load data');
    }
  };

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;
  @ViewChild(MatTable, {static: true}) table: MatTable<any>;

  constructor(private activatedRoute: ActivatedRoute, private logsService: LogsService, private router: Router,
              private snackbarService: SnackbarService, private changeDetectorRef: ChangeDetectorRef,
              private authService: AuthorizationService) {
    this.severities = ['EMERGENCY', 'ALERT', 'CRITICAL', 'ERROR', 'WARNING', 'NOTICE', 'INFORMATIONAL', 'DEBUG'];
    this.facilities = ['KERN', 'USER', 'MAIL', 'DAEMON', 'AUTH', 'SYSLOG', 'LPR', 'NEWS',
      'UUCP', 'CLOCK_DAEMON', 'AUTHPRIV', 'FTP', 'NTP', 'LOGAUDIT', 'LOGALERT',
      'CRON', 'LOCAL0', 'LOCAL1', 'LOCAL2', 'LOCAL3', 'LOCAL4', 'LOCAL5', 'LOCAL6', 'LOCAL7'];
    this.searchParameters = {
      messageRegex: '',
      hostIPRegex: '',
      hostname: '',
      startDate: null,
      endDate: null,
      severity: null,
      facility: null
    };
    this.activeSearchParameters = {
      messageRegex: '',
      hostIPRegex: '',
      hostname: '',
      startDate: null,
      endDate: null,
      severity: null,
      facility: null
    };
  }

  ngOnInit() {
    this.displayedColumns = ['timestamp', 'hostIP', 'sourceIP', 'severity', 'facility', 'system', 'hostname', 'message'];
    this.getData();
  }

  private getData() {
    this.logsService.searchLogs(this.searchParameters, 0, 5).subscribe(this.observer);
  }

  private onSubmit() {
    Object.assign(this.activeSearchParameters, this.searchParameters);
    this.searchLogs(0, 5);
  }

  private searchLogs(page: number, size: number) {
    const parameters: SearchLogsDTO = {
      messageRegex: this.activeSearchParameters.messageRegex,
      hostIPRegex: this.activeSearchParameters.hostIPRegex,
      hostname: this.activeSearchParameters.hostname,
      startDate: this.activeSearchParameters.startDate,
      endDate: this.activeSearchParameters.endDate,
      severity: this.activeSearchParameters.severity,
      facility: this.activeSearchParameters.facility
    };
    if (parameters.startDate !== '') {
      parameters.startDate = moment(parameters.startDate).format('yyyy-MM-dd\'T\'HH:mm:ss.SSSXXX');
    }
    if (parameters.endDate !== '') {
      parameters.endDate = moment(parameters.endDate).format('yyyy-MM-dd\'T\'HH:mm:ss.SSSXXX');
    }
    this.logsService.searchLogs(parameters, page, size).subscribe({
      next: (result: Page) => {
        this.logs.data = result.content;
      },
      error: (message: string) => {
        this.snackbarService.displayMessage(JSON.parse(JSON.stringify(message)).error);
      }
    });
  }

  private resetForm(form) {
    form.reset();
    this.searchParameters = {
      messageRegex: '',
      hostIPRegex: '',
      hostname: '',
      startDate: null,
      endDate: null,
      severity: null,
      facility: null
    };
    Object.assign(this.activeSearchParameters, this.searchParameters);
    this.searchLogs(0, 5);
  }
}
