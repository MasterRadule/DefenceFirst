import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SearchLogsDTO} from '../model/search-logs-dto';
import {ReportLogsDTO} from '../model/report-logs-dto';

@Injectable({
  providedIn: 'root'
})
export class LogsService {

  constructor(private http: HttpClient) {
  }

  searchLogs(parameters: SearchLogsDTO, page: number, size: number) {
    // tslint:disable-next-line:max-line-length
    return this.http.get(`log/search?messageRegex=${parameters.messageRegex}&hostIPRegex=${parameters.hostIPRegex}&hostname=${parameters.hostname}&severity=${parameters.severity}&facility=${parameters.facility}&startDate=${parameters.startDate}&endDate=${parameters.endDate}&page=${page}&size=${size}`);
  }

  getReportBySystem(parameters: ReportLogsDTO) {
    return this.http.get(`log/report/system?startDate=${parameters.startDate}&endDate=${parameters.endDate}&system=${parameters.system}`);
  }

  getReportByMachine(parameters: ReportLogsDTO) {
    // tslint:disable-next-line:max-line-length
    return this.http.get(`log/report/machine?startDate=${parameters.startDate}&endDate=${parameters.endDate}&machine=${parameters.machine}`);
  }
}
