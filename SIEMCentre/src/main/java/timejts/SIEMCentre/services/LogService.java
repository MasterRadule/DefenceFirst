package timejts.SIEMCentre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.repository.LogRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class LogService {

    @Autowired
    LogRepository logRepository;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

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
        Date startDate = null;
        Date endDate = null;
        String startDateStr;
        String endDateStr;
        if (!searchDTO.getStartDate().equals("") && !searchDTO.getEndDate().equals("")) {
            startDateStr = searchDTO.getStartDate().replace(" ", "+");
            endDateStr = searchDTO.getEndDate().replace(" ", "+");
            startDate = formatter.parse(startDateStr);
            endDate = formatter.parse(endDateStr);
        }
        PageRequest pageRequest = PageRequest
                .of(page.getPageNumber(), page.getPageSize(), Sort.by("timestamp").descending());
        return logRepository
                .searchLogs(searchDTO.getMessageRegex(), searchDTO.getHostIPRegex(), searchDTO.getHostname(), startDate,
                        endDate, searchDTO.getSeverity(), searchDTO.getFacility(), pageRequest);
    }
}
