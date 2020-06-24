import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

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
}
