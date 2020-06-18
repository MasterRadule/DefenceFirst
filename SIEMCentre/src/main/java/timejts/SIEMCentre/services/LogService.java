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

import java.util.ArrayList;

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


    public Page<Log> searchLogs(SearchLogsDTO searchDTO, Pageable page) {
        PageRequest pageRequest = PageRequest
                .of(page.getPageNumber(), page.getPageSize(), Sort.by("timestamp").descending());
        return logRepository
                .searchLogs(searchDTO.getMessageRegex(), searchDTO.getHostIPRegex(), searchDTO.getHostname(), searchDTO
                        .getStartDate(), searchDTO.getEndDate(), searchDTO.getSeverity(), searchDTO
                        .getFacility(), pageRequest);
    }
}
