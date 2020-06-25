import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AlarmDTO} from '../model/alarm-dto';
import {ReportLogsDTO} from '../model/report-logs-dto';
import {ReportAlarmsDTO} from '../model/report-alarms-dto';

@Injectable({
  providedIn: 'root'
})
export class AlarmsService {

  constructor(private http: HttpClient) {
  }

  getAlarms(page: number, size: number) {
    return this.http.get(`alarm?page=${page}&size=${size}`);
  }

  getRaisedAlarms(page: number, size: number) {
    return this.http.get(`alarm/raised?page=${page}&size=${size}`);
  }

  createAlarm(alarm: AlarmDTO) {
    return this.http.post('alarm', alarm, {responseType: 'text'});
  }

  getReportBySeverity(parameters: ReportAlarmsDTO) {
    // tslint:disable-next-line:max-line-length
    return this.http.get(`alarm/report/severity?startDate=${parameters.startDate}&endDate=${parameters.endDate}&severity=${parameters.severity}`);
  }

  getReportByFacility(parameters: ReportAlarmsDTO) {
    // tslint:disable-next-line:max-line-length
    return this.http.get(`alarm/report/facility?startDate=${parameters.startDate}&endDate=${parameters.endDate}&facility=${parameters.facility}`);
  }

  getReportByAlarmType(parameters: ReportAlarmsDTO) {
    // tslint:disable-next-line:max-line-length
    return this.http.get(`alarm/report/alarm-type?startDate=${parameters.startDate}&endDate=${parameters.endDate}&alarmType=${parameters.alarmType}`);
  }
}
