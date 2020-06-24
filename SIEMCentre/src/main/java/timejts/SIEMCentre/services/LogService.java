package timejts.SIEMCentre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import timejts.SIEMCentre.dto.ReportLogsDTO;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.Severity;
import timejts.SIEMCentre.repository.LogRepository;
import timejts.SIEMCentre.utils.Utilities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

@Service
public class LogService {

    @Autowired
    LogRepository logRepository;

    public String saveLogs(ArrayList<Log> logs) {
        logs.forEach(log -> {
            logRepository.save(log);
            AlarmService.kieSession.insert(log);
        });

        return "Success";
    }

    public Page<Log> getLogs(Pageable page) {
        return logRepository.findAllByOrderByTimestampDesc(page);
    }


    public Page<Log> searchLogs(SearchLogsDTO searchDTO, Pageable page) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(searchDTO.getStartDate(), searchDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        PageRequest pageRequest = PageRequest
                .of(page.getPageNumber(), page.getPageSize(), Sort.by("timestamp").descending());
        return logRepository
                .searchLogs(searchDTO.getMessageRegex(), searchDTO.getHostIPRegex(), searchDTO.getHostname(), startDate,
                        endDate, searchDTO.getSeverity() == Severity.NA ? null : searchDTO.getSeverity(), searchDTO
                                .getFacility() == Facility.NA ? null : searchDTO.getFacility(), pageRequest);
    }

    public Long getReportBySystem(ReportLogsDTO reportLogsDTO) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(reportLogsDTO.getStartDate(), reportLogsDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        return logRepository
                .countBySystemEqualsAndTimestampBetween(reportLogsDTO.getSystem(), startDate, endDate);
    }

    public Long getReportByMachine(ReportLogsDTO reportLogsDTO) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(reportLogsDTO.getStartDate(), reportLogsDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        return logRepository
                .countByHostIPEqualsAndTimestampBetween(reportLogsDTO.getMachine(), startDate, endDate);
    }
}
