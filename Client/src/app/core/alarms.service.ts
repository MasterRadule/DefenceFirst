import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AlarmDTO} from '../model/alarm-dto';

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
}
