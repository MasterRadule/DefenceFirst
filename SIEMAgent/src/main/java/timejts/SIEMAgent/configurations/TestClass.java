package timejts.SIEMAgent.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public void proba() throws InterruptedException {
        Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
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


        Alarm alarm = new Alarm(null, 2, 1, "", Severity.INFORMATIONAL, Facility.AUTH, "", "");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.EXCEEDED_NUMBER_OF_REQUESTS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());
    }
}
