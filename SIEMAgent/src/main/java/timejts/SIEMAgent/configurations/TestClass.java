package timejts.SIEMAgent.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class TestClass {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    RestTemplate restTemplate;

    @PostConstruct
    public void proba() {
        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());
    }
}
