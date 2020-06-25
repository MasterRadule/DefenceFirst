package timejts.SIEMCentre.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.dto.ReportAlarmsDTO;
import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.RaisedAlarm;
import timejts.SIEMCentre.services.AlarmService;

import javax.validation.Valid;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    @PostMapping
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> createAlarm(@RequestBody @Valid AlarmDTO alarmDTO) {
        try {
            return new ResponseEntity<>(alarmService.createAlarm(alarmDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Page<Alarm>> getAlarms(Pageable pageable) {
        return new ResponseEntity<>(alarmService.getAlarms(pageable), HttpStatus.OK);
    }

    @GetMapping("/raised")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Page<RaisedAlarm>> getRaisedAlarms(Pageable pageable) {
        return new ResponseEntity<>(alarmService.getRaisedAlarms(pageable), HttpStatus.OK);
    }

    @GetMapping("/report/severity")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getReportBySeverity(ReportAlarmsDTO reportAlarmsDTO) {
        try {
            return new ResponseEntity<>(alarmService.getReportBySeverity(reportAlarmsDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/report/facility")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getReportByFacility(ReportAlarmsDTO reportAlarmsDTO) {
        try {
            return new ResponseEntity<>(alarmService.getReportByFacility(reportAlarmsDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/report/alarm-type")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getReportByAlarmType(ReportAlarmsDTO reportAlarmsDTO) {
        try {
            return new ResponseEntity<>(alarmService.getReportByAlarmType(reportAlarmsDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
