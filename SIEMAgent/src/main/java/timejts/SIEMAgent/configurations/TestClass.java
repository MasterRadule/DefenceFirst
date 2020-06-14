package timejts.SIEMAgent.configurations;

import com.sun.jna.platform.win32.Advapi32Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class TestClass {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    RestTemplate restTemplate;

   /* @PostConstruct
    public void proba() {
        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());
    }*/

    @PostConstruct
    public void proba() {
        int lastRecordNumber = 0;

        Advapi32Util.EventLogIterator iterator;
        while (true) {
            iterator = new Advapi32Util.EventLogIterator("Application");

            while (iterator.hasNext()) {
                Advapi32Util.EventLogRecord record = iterator.next();
                if (lastRecordNumber >= record.getRecordNumber())
                    continue;

                System.out.println(record.getRecordNumber()
                        + ": Event ID: " + record.getEventId()
                        + ", Event Type: " + record.getType()
                        + ", Event Source: " + record.getSource()
                        + ", Event time: " + new Date(record.getRecord().TimeGenerated.longValue() * 1000L)
                        + "Event data: " + record.getData());
                lastRecordNumber = record.getRecordNumber();
                //counter++;
                //if (counter > 10) {
                //    break;
                //}
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
