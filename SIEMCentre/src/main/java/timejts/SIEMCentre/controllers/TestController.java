package timejts.SIEMCentre.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TestController {

    @PostMapping("/Test")
    @Secured("ROLE_AGENT")
    public ResponseEntity<String> TestSSL(@RequestBody String message) {
        System.out.println("Message from agent: " + message);
        return new ResponseEntity<>("Response from CENTER", HttpStatus.OK);
    }
}
