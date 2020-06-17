package timejts.SIEMCentre.services;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.dto.AlarmDataDTO;
import timejts.SIEMCentre.dto.SearchLogsDTO;
import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.RaisedAlarm;
import timejts.SIEMCentre.repository.AlarmRepository;
import timejts.SIEMCentre.repository.LogRepository;
import timejts.SIEMCentre.repository.RaisedAlarmRepository;
import timejts.SIEMCentre.utils.Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

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
