package timejts.SIEMCentre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        logs.forEach(log -> logRepository.save(log));

        return "Success";
    }

    public Page<Log> getLogs(Pageable page) {
        return logRepository.findAll(page);
    }


    public Page<Log> searchLogs(SearchLogsDTO searchDTO, Pageable page) {
        return logRepository
                .searchLogs(searchDTO.getMessageRegex(), searchDTO.getHostIPRegex(), searchDTO.getHostname(), searchDTO
                        .getStartDate(), searchDTO.getEndDate(), searchDTO.getSeverity(), searchDTO
                        .getFacility(), page);
    }


}
