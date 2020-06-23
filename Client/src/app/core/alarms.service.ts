import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AlarmsService {

  constructor(private http: HttpClient) {
  }

  getAlarms() {
    console.log("http");
    return this.http.get('alarm?page=0&size=5');
  }

  getRaisedAlarms() {
    return this.http.get('alarm/raised?page=0&size=5');
  }
}
