import {Component, OnInit} from '@angular/core';
import {AlarmDTO} from '../../model/alarm-dto';
import {Alarm} from '../../model/alarm';
import {SnackbarService} from '../../core/snackbar.service';
import {AlarmsService} from '../../core/alarms.service';

@Component({
  selector: 'app-alarm-creation',
  templateUrl: './alarm-creation.component.html',
  styleUrls: ['./alarm-creation.component.css']
})
export class AlarmCreationComponent implements OnInit {
  private severities: string[];
  private facilities: string[];
  private alarm: AlarmDTO;
  private observer = {
    next: (result: string) => {
      this.snackbarService.displayMessage(result);
    },
    error: () => {
      this.snackbarService.displayMessage('Failed to load data');
    }
  };

  constructor(private snackbarService: SnackbarService, private alarmService: AlarmsService) {
    this.severities = ['EMERGENCY', 'ALERT', 'CRITICAL', 'ERROR', 'WARNING', 'NOTICE', 'INFORMATIONAL', 'DEBUG'];
    this.facilities = ['KERN', 'USER', 'MAIL', 'DAEMON', 'AUTH', 'SYSLOG', 'LPR', 'NEWS',
      'UUCP', 'CLOCK_DAEMON', 'AUTHPRIV', 'FTP', 'NTP', 'LOGAUDIT', 'LOGALERT',
      'CRON', 'LOCAL0', 'LOCAL1', 'LOCAL2', 'LOCAL3', 'LOCAL4', 'LOCAL5', 'LOCAL6', 'LOCAL7'];

    this.resetAlarm();
  }

  ngOnInit() {
  }

  createAlarm(type: string) {
    this.alarm.alarmType = type;
    this.alarmService.createAlarm(this.alarm).subscribe(this.observer);
  }

  resetAlarm() {
    const alarmVar: Alarm = {
      count: 0,
      timespan: 0,
      sourceIPRegex: '',
      severity: 'NA',
      facility: 'NA',
      messageRegex1: '',
      messageRegex2: ''
    };
    this.alarm = {alarm: alarmVar, alarmType: ''};
  }
}
