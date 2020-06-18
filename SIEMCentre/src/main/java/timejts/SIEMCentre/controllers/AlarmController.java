package timejts.SIEMCentre.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.RaisedAlarm;
import timejts.SIEMCentre.services.AlarmService;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    @PostMapping
    public ResponseEntity<String> createAlarm(@RequestBody AlarmDTO alarmDTO) {
        try {
            return new ResponseEntity<>(alarmService.createAlarm(alarmDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Alarm>> getAlarms(Pageable pageable) {
        return new ResponseEntity<>(alarmService.getAlarms(pageable), HttpStatus.OK);
    }

    @GetMapping("/raised")
    public ResponseEntity<Page<RaisedAlarm>> getRaisedAlarms(Pageable pageable) {
        return new ResponseEntity<>(alarmService.getRaisedAlarms(pageable), HttpStatus.OK);
    }
}
