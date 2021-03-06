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
  private totalElements: number;
  private severities: string[];
  private facilities: string[];
  private observer = {
    next: (logs: Page) => {
      this.logs.data = logs.content;
      this.totalElements = logs.totalElements;
      this.changeDetectorRef.detectChanges();
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
      startDate: '',
      endDate: '',
      severity: 'NA',
      facility: 'NA'
    };
    this.activeSearchParameters = {
      messageRegex: '',
      hostIPRegex: '',
      hostname: '',
      startDate: '',
      endDate: '',
      severity: 'NA',
      facility: 'NA'
    };
  }

  ngOnInit() {
    this.displayedColumns = ['timestamp', 'hostIP', 'sourceIP', 'system', 'hostname', 'severity', 'facility', 'message'];
    this.getData(null);
  }

  private getData($event) {
    if ($event == null) {
      this.paginator.pageSize = 6;
    }
    this.logsService.searchLogs(this.searchParameters, this.paginator.pageIndex, this.paginator.pageSize).subscribe(this.observer);
  }

  private onSubmit() {
    Object.assign(this.activeSearchParameters, this.searchParameters);
    this.searchLogs();
  }

  private searchLogs() {
    const parameters: SearchLogsDTO = {
      messageRegex: this.activeSearchParameters.messageRegex,
      hostIPRegex: this.activeSearchParameters.hostIPRegex,
      hostname: this.activeSearchParameters.hostname,
      startDate: this.activeSearchParameters.startDate,
      endDate: this.activeSearchParameters.endDate,
      severity: this.activeSearchParameters.severity,
      facility: this.activeSearchParameters.facility
    };
    if (parameters.startDate === 'Invalid date') {
      parameters.startDate = '';
    }
    if (parameters.endDate === 'Invalid date') {
      parameters.endDate = '';
    }
    if (parameters.startDate !== '' && parameters.startDate !== null) {
      parameters.startDate = moment(parameters.startDate).format('YYYY-MM-DD HH:mm:ss');
    }
    if (parameters.endDate !== '' && parameters.endDate !== null) {
      parameters.endDate = moment(parameters.endDate).format('YYYY-MM-DD HH:mm:ss');
    }
    this.paginator.pageIndex = 0;
    this.logsService.searchLogs(parameters, 0, 6).subscribe(this.observer);
  }

  private resetForm(form) {
    form.reset();
    this.searchParameters = {
      messageRegex: '',
      hostIPRegex: '',
      hostname: '',
      startDate: '',
      endDate: '',
      severity: 'NA',
      facility: 'NA'
    };
    Object.assign(this.activeSearchParameters, this.searchParameters);
    this.searchLogs();
  }
}
