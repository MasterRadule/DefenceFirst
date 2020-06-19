package timejts.SIEMAgent.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timejts.SIEMCentre.dto.AlarmDTO;
import timejts.SIEMCentre.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;

@Component
public class TestClass {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    RestTemplate restTemplate;

    @PostConstruct
    public void alarms() throws InterruptedException {

        System.out.println("Proba");

        /*######## EXCEEDED NUMBER OF REQUESTS  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l1.getTimestamp());
        Thread.sleep(2000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l2.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);
        logs.add(l2);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 10, "", Severity.INFORMATIONAL, Facility.AUTH, "", "");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.EXCEEDED_NUMBER_OF_REQUESTS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## SUSPICIOUS BEHAVIOUR  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        Thread.sleep(2000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "503", "Application");
        System.out.println(l2.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);
        logs.add(l2);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "", Severity.INFORMATIONAL, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.SUSPICIOUS_BEHAVIOUR);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## SEVERITY ALARM  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.ERROR,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "", Severity.ERROR, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.SEVERITY_ALARM);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## MALICIOUS IP ADDRESS  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "192.168.8.1", "192.168.8.1", Severity.ERROR,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "\"192.168.8.1\"", Severity.ERROR, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.MALICIOUS_IP_ADDRESS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/
    }

    private String messageRegex;
    private String hostname;
    private String hostIPRegex;
    private Date startDate;
    private Date endDate;
    private Severity severity;
    private Facility facility;

    @PostConstruct
    public void logs() {
        ResponseEntity<Object> response =
                restTemplate.getForEntity("https://localhost:8082/log/search?messageRegex&hostname" +
                        "&hostIPRegex&startDate=2020-06-18T13:58:03.732+00:00&endDate=2020-06-18T19:01:11.728+00:00&severity&facility&page=0&size=5", Object.class);
        System.out.println(response.getBody());
    }
}
