import {Component, OnInit} from '@angular/core';
import {ReportAlarmsDTO} from '../model/report-alarms-dto';
import {ReportLogsDTO} from '../model/report-logs-dto';
import {SnackbarService} from '../core/snackbar.service';
import {LogsService} from '../core/logs.service';
import {AlarmsService} from '../core/alarms.service';
import * as moment from 'moment';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  private severities: string[];
  private facilities: string[];
  private alarmTypes: string[];
  private reportLogsDTO: ReportLogsDTO;
  private reportAlarmsDTO: ReportAlarmsDTO;
  private observer = {
    next: (result: number) => {
      this.snackbarService.displayMessage('Report: ' + result);
    },
    error: () => {
      this.snackbarService.displayMessage('Failed to load data');
    }
  };

  constructor(private snackbarService: SnackbarService, private logsService: LogsService, private alarmsService: AlarmsService) {
    this.severities = ['EMERGENCY', 'ALERT', 'CRITICAL', 'ERROR', 'WARNING', 'NOTICE', 'INFORMATIONAL', 'DEBUG'];
    this.facilities = ['KERN', 'USER', 'MAIL', 'DAEMON', 'AUTH', 'SYSLOG', 'LPR', 'NEWS',
      'UUCP', 'CLOCK_DAEMON', 'AUTHPRIV', 'FTP', 'NTP', 'LOGAUDIT', 'LOGALERT',
      'CRON', 'LOCAL0', 'LOCAL1', 'LOCAL2', 'LOCAL3', 'LOCAL4', 'LOCAL5', 'LOCAL6', 'LOCAL7'];
    this.alarmTypes = ['EXCEEDED_NUMBER_OF_REQUESTS', 'SUSPICIOUS_BEHAVIOUR', 'SEVERITY_ALARM', 'MALICIOUS_IP_ADDRESS'];

    this.resetDTO();
  }

  ngOnInit() {
  }

  resetDTO() {
    this.reportLogsDTO = {startDate: '', endDate: '', system: '', machine: ''};
    this.reportAlarmsDTO = {startDate: '', endDate: '', severity: 'NA', facility: 'NA', alarmType: ''};
  }

  getReportsBySystem() {
    if (!this.checkDates(this.reportLogsDTO)) {
      return;
    }
    this.logsService.getReportBySystem(this.reportLogsDTO).subscribe(this.observer);
  }

  getReportsByMachine() {
    if (!this.checkDates(this.reportLogsDTO)) {
      return;
    }
    this.logsService.getReportByMachine(this.reportLogsDTO).subscribe(this.observer);
  }

  getReportsBySeverity() {
    if (!this.checkDates(this.reportAlarmsDTO)) {
      return;
    }
    this.alarmsService.getReportBySeverity(this.reportAlarmsDTO).subscribe(this.observer);
  }

  getReportsByFacility() {
    if (!this.checkDates(this.reportAlarmsDTO)) {
      return;
    }
    this.alarmsService.getReportByFacility(this.reportAlarmsDTO).subscribe(this.observer);
  }

  getReportsByAlarmType() {
    if (!this.checkDates(this.reportAlarmsDTO)) {
      return;
    }
    this.alarmsService.getReportByAlarmType(this.reportAlarmsDTO).subscribe(this.observer);
  }

  checkDates(dto: ReportLogsDTO | ReportAlarmsDTO) {
    if (dto.startDate === 'Invalid date' || dto.startDate === '') {
      dto.startDate = '';
      this.snackbarService.displayMessage('Start date must be specified');
      return false;
    }
    if (dto.endDate === 'Invalid date' || dto.endDate === '') {
      dto.endDate = '';
      this.snackbarService.displayMessage('End date must be specified');
      return false;
    }
    if (dto.startDate !== '' && dto.startDate !== null) {
      dto.startDate = moment(dto.startDate).format('YYYY-MM-DD HH:mm:ss');
    }
    if (dto.endDate !== '' && dto.endDate !== null) {
      dto.endDate = moment(dto.endDate).format('YYYY-MM-DD HH:mm:ss');
    }

    return true;
  }

}
