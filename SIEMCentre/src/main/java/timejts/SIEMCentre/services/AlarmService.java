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
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.dto.AlarmDataDTO;
import timejts.SIEMCentre.dto.ReportAlarmsDTO;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AlarmService {

    public static KieSession kieSession;
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

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSessions() {
        kieSession = getKieSession();
    }

    private KieSession getKieSession() {
        return kieContainer.newKieSession("rules-session");
    }

    public String createAlarm(AlarmDTO alarmDTO) throws IOException, MavenInvocationException, InterruptedException {
        alarmRepository.save(alarmDTO.getAlarm());
        generateAlarmRule(alarmDTO);

        return "Alarm successfully created";
    }

    public Page<Alarm> getAlarms(Pageable pageable) {
        return alarmRepository.findAllByOrderByTimespanDesc(pageable);
    }

    public Page<RaisedAlarm> getRaisedAlarms(Pageable pageable) {
        return raisedAlarmRepository.findAllByOrderByTimeDesc(pageable);
    }

    private void generateAlarmRule(AlarmDTO alarmDTO) throws IOException, MavenInvocationException, InterruptedException {
        String templatePath = null;
        String drlPath = null;
        InputStream template = null;
        AlarmDataDTO dataDTO = null;

        switch (alarmDTO.getAlarmType()) {
            case EXCEEDED_NUMBER_OF_REQUESTS:
                templatePath = exceededNumOfRequestsTemplate;
                drlPath = exceededNumOfRequestsDRLPath + exceededNumOfRequestsCounter + ".drl";
                template = new FileInputStream(templatePath);
                alarmDTO.getAlarm().setCount(alarmDTO.getAlarm().getCount() - 1);
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
        System.out.println("checkForAlarms");
        QueryResults results = kieSession.getQueryResults("Get logs");
        System.out.println("Num of logs: " + results.size());
        System.out.println("Fact count:" + kieSession.getFactCount());
        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        kieSession.fireAllRules();
    }

    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    private void getRaisedAlarmsFromSession() {
        System.out.println("getRaisedAlarmsFromSession");
        QueryResults results3 = kieSession.getQueryResults("Get logs");
        System.out.println("Num of logs: " + results3.size());
        System.out.println("Fact count:" + kieSession.getFactCount());

        QueryResults results = kieSession.getQueryResults("Get new raised alarms");
        if (results.size() == 0) {
            return;
        }

        RaisedAlarm ra;
        for (QueryResultsRow queryResult : results) {
            ra = (RaisedAlarm) queryResult.get("$a");
            System.out.println(ra.getAlarmType().toString());
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

    public Long getReportBySeverity(ReportAlarmsDTO reportAlarmsDTO) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(reportAlarmsDTO.getStartDate(), reportAlarmsDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        return raisedAlarmRepository
                .countBySeverityEqualsAndTimeBetween(reportAlarmsDTO.getSeverity(), startDate, endDate);
    }

    public Long getReportByFacility(ReportAlarmsDTO reportAlarmsDTO) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(reportAlarmsDTO.getStartDate(), reportAlarmsDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        return raisedAlarmRepository
                .countByFacilityEqualsAndTimeBetween(reportAlarmsDTO.getFacility(), startDate, endDate);
    }

    public Long getReportByAlarmType(ReportAlarmsDTO reportAlarmsDTO) throws ParseException {
        Date startDate;
        Date endDate;
        Pair<Date, Date> dates = Utilities.parseDates(reportAlarmsDTO.getStartDate(), reportAlarmsDTO.getEndDate());
        if (dates == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = dates.getFirst();
            endDate = dates.getSecond();
        }
        return raisedAlarmRepository
                .countByAlarmTypeEqualsAndTimeBetween(reportAlarmsDTO.getAlarmType(), startDate, endDate);
    }
}
