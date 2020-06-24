import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SearchLogsDTO} from '../model/search-logs-dto';

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
}
