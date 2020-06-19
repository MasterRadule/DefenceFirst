package timejts.SIEMCentre.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.services.LogService;

import java.util.ArrayList;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    LogService logService;

    @PostMapping
    public ResponseEntity<String> sendLogs(@RequestBody ArrayList<Log> logs) {
        return new ResponseEntity<>(logService.saveLogs(logs), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Log>> getLogs(Pageable pageable) {
        return new ResponseEntity<>(logService.getLogs(pageable), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchLogs(SearchLogsDTO searchDTO, Pageable pageable) {
        try {
            return new ResponseEntity<>(logService.searchLogs(searchDTO, pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
