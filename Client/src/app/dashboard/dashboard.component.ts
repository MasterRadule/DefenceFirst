import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private content: string;

  constructor(private route: ActivatedRoute) {
    route.paramMap.subscribe((params: ParamMap) => {
      this.content = params.get('content');
    });
  }

  ngOnInit() {
  }

}
