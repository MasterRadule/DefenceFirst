package timejts.SIEMCentre.services;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.dto.AlarmDataDTO;
import timejts.SIEMCentre.model.Alarm;
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
public class AlarmService {

    @Value("${rules.drt.exceededNumOfRequestsTemplate}")
    private String exceededNumOfRequestsTemplate;

    @Value("${rules.drt.suspiciousBehaviourTemplate}")
    private String suspiciousBehaviourTemplate;

    @Value("${rules.drt.severityTemplate}")
    private String severityTemplate;

    @Value("${rules.drt.maliciousTemplate}")
    private String maliciousTemplate;

    @Value("${rules.drt.exceededNumOfRequestsDRLPath}")
    private String exceededNumOfRequestsDRLPath;

    @Value("${rules.drt.suspiciousBehaviourDRLPath}")
    private String suspiciousBehaviourDRLPath;

    @Value("${rules.drt.severityDRLPath}")
    private String severityDRLPath;

    @Value("${rules.drt.maliciousDRLPath}")
    private String maliciousDRLPath;

    private static int exceededNumOfRequestsCounter = 0;
    private static int suspiciousBehaviourCounter = 0;
    private static int severityCounter = 0;
    private static int maliciousCounter = 0;

    @Autowired
    LogRepository logRepository;

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    RaisedAlarmRepository raisedAlarmRepository;

    @Autowired
    KieContainer kieContainer;

    private KieSession kieSession;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSessions() {
        kieSession = getKieSession();
    }

    private KieSession getKieSession() {
        return kieContainer.newKieSession("rules-session");
    }

    public String createAlarm(AlarmDTO alarmDTO) throws IOException, MavenInvocationException {
        alarmRepository.save(alarmDTO.getAlarm());
        generateAlarmRule(alarmDTO);

        return "Alarm successfully created";
    }

    public Page<Alarm> getAlarms(Pageable pageable) {
        return alarmRepository.findAll(pageable);
    }

    public Page<RaisedAlarm> getRaisedAlarms(Pageable pageable) {
        return raisedAlarmRepository.findAll(pageable);
    }

    private void generateAlarmRule(AlarmDTO alarmDTO) throws IOException, MavenInvocationException {
        String templatePath;
        String drlPath = null;
        InputStream template = null;
        AlarmDataDTO dataDTO = null;

        switch (alarmDTO.getAlarmType()) {
            case EXCEEDED_NUMBER_OF_REQUESTS:
                templatePath = exceededNumOfRequestsTemplate;
                drlPath = exceededNumOfRequestsDRLPath + exceededNumOfRequestsCounter + ".drl";
                template = new FileInputStream(templatePath);
                dataDTO = new AlarmDataDTO(exceededNumOfRequestsCounter, alarmDTO.getAlarm());
                exceededNumOfRequestsCounter++;
                break;
            case SUSPICIOUS_BEHAVIOUR:
                templatePath = suspiciousBehaviourTemplate;
                drlPath = suspiciousBehaviourDRLPath + suspiciousBehaviourCounter + ".drl";
                template = new FileInputStream(templatePath);
                dataDTO = new AlarmDataDTO(suspiciousBehaviourCounter, alarmDTO.getAlarm());
                suspiciousBehaviourCounter++;
                break;
            case SEVERITY_ALARM:
                templatePath = severityTemplate;
                drlPath = severityDRLPath + severityCounter + ".drl";
                template = new FileInputStream(templatePath);
                dataDTO = new AlarmDataDTO(severityCounter, alarmDTO.getAlarm());
                severityCounter++;
                break;
            case MALICIOUS_IP_ADDRESS:
                templatePath = maliciousTemplate;
                drlPath = maliciousDRLPath + maliciousCounter + ".drl";
                template = new FileInputStream(templatePath);
                dataDTO = new AlarmDataDTO(maliciousCounter, alarmDTO.getAlarm());
                maliciousCounter++;
                break;
        }

        List<AlarmDataDTO> data = new ArrayList<>();
        data.add(dataDTO);
        String drl = (new ObjectDataCompiler()).compile(data, template);

        Files.write(Paths.get(drlPath), drl.getBytes(), StandardOpenOption.CREATE);

        Utilities.mavenCleanAndInstall();
    }

    @Scheduled(fixedRate = 2000, initialDelay = 10000)
    private void checkForAlarms() {
        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        kieSession.fireAllRules();
    }

    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    private void getRaisedAlarmsFromSession() {
        QueryResults results = kieSession.getQueryResults("Get new raised alarms");
        if (results.size() == 0) {
            return;
        }

        RaisedAlarm ra;
        for (QueryResultsRow queryResult : results) {
            ra = (RaisedAlarm) queryResult.get("$a");
            raisedAlarmRepository.save(ra);
        }

        kieSession.getAgenda().getAgendaGroup("raise-alarm").setFocus();
        kieSession.fireAllRules();

        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();

        for (QueryResultsRow queryResult : results) {
            ra = (RaisedAlarm) queryResult.get("$a");
            // TODO: 17.6.2020. Send notice through web socket
        }
    }
}
