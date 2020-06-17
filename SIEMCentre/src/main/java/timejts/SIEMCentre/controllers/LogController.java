package timejts.SIEMCentre.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.services.LogService;

import java.util.ArrayList;

@Controller
@RequestMapping("/logs")
public class LogController {

    @Autowired
    LogService logService;

    @PostMapping()
    public ResponseEntity<String> sendLogs(@RequestBody ArrayList<Log> logs) {
        return new ResponseEntity<>(logService.saveLogs(logs), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<Log>> getLogs(Pageable pageable) {
        return new ResponseEntity<>(logService.getLogs(pageable), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Log>> searchLogs(SearchLogsDTO searchDTO, Pageable pageable) {
        return new ResponseEntity<>(logService.searchLogs(searchDTO, pageable), HttpStatus.OK);
    }
}
