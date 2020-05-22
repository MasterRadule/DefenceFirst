package timejts.SIEMCentre.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TestController {

    @PostMapping("/Test")
    public ResponseEntity<String> TestSSL(@RequestBody String message) {
        System.out.println("Message from agent: " +  message);
        return new ResponseEntity<String>(new String("Response from CENTER"), HttpStatus.OK);
    }
}
