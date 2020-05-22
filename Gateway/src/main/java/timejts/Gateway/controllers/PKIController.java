package timejts.Gateway.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/certificates")
public class PKIController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<Object> getCertificates() {
        try {
            return restTemplate.getForEntity("https://localhost:8443/api/certificates", Object.class);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
