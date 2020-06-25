package timejts.SIEMCentre.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import timejts.SIEMCentre.dto.ReportLogsDTO;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.dto.SignedLogsDTO;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.services.LogService;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    LogService logService;

    @PostMapping
    @Secured("ROLE_AGENT")
    public ResponseEntity<String> sendLogs(@RequestBody SignedLogsDTO signedLogsDTO,
                                           @RequestHeader("serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(logService.saveLogs(signedLogsDTO, serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Page<Log>> getLogs(Pageable pageable) {
        return new ResponseEntity<>(logService.getLogs(pageable), HttpStatus.OK);
    }

    @GetMapping("/search")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> searchLogs(SearchLogsDTO searchDTO, Pageable pageable) {
        try {
            return new ResponseEntity<>(logService.searchLogs(searchDTO, pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/report/system")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getReportBySystem(ReportLogsDTO reportLogsDTO) {
        try {
            return new ResponseEntity<>(logService.getReportBySystem(reportLogsDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/report/machine")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getReportByMachine(ReportLogsDTO reportLogsDTO) {
        try {
            return new ResponseEntity<>(logService.getReportByMachine(reportLogsDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
