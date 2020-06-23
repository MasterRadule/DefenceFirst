import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTable, MatTableDataSource} from '@angular/material';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {SnackbarService} from '../../core/snackbar.service';
import {AuthorizationService} from '../../core/authorization.service';
import {AlarmsService} from '../../core/alarms.service';
import {Page} from '../../model/page';

@Component({
  selector: 'app-alarm-list',
  templateUrl: './alarm-list.component.html',
  styleUrls: ['./alarm-list.component.css']
})
export class AlarmListComponent implements OnInit {
  private alarms: MatTableDataSource<any> = new MatTableDataSource<any>([]);
  displayedColumns: string[];
  private content: string;
  private observer = {
    next: (alarms: Page) => {
      this.alarms.data = alarms.content;
      this.changeDetectorRef.detectChanges();
      this.alarms.paginator = this.paginator;
      this.alarms.sort = this.sort;
    },
    error: () => {
      this.snackbarService.displayMessage('Failed to load data');
    }
  };

  @ViewChild(MatPaginator, {static: true}) private paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) private sort: MatSort;
  @ViewChild(MatTable, {static: true}) table: MatTable<any>;

  constructor(private activatedRoute: ActivatedRoute, private alarmsService: AlarmsService, private router: Router,
              private snackbarService: SnackbarService, private changeDetectorRef: ChangeDetectorRef,
              private authService: AuthorizationService) {
  }

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe((params: ParamMap) => {
        this.content = params.get('tab-content');
        if (this.content === 'rules') {
          this.displayedColumns = ['count', 'timespan', 'sourceIPRegex', 'severity', 'facility', 'messageRegex1', 'messageRegex2'];
        } else {
          this.displayedColumns = ['time', 'alarmType', 'sourceIP', 'severity', 'facility', 'message1', 'message2'];
        }
        this.getData();
      }
    );
  }

  private getData() {
    if (this.content === 'rules') {
      this.alarmsService.getAlarms().subscribe(this.observer);
    } else {
      this.alarmsService.getRaisedAlarms().subscribe(this.observer);
    }
  }
}
