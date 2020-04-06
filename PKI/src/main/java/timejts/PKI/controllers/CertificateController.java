package timejts.PKI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timejts.PKI.dto.CertAuthorityDTO;
import timejts.PKI.services.CertificateService;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    CertificateService certificateService;

    @PostMapping("/submit-csr")
    public ResponseEntity<Object> submitCSR(@RequestBody byte[] csrData) {
        try {
            return new ResponseEntity<>(certificateService.submitCSR(csrData), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-non-ca-certificate")
    public ResponseEntity<Object> createNonCACertificate(@RequestParam String commonName, @RequestParam String caName) {
        try {
            return new ResponseEntity<>(certificateService.createNonCACertificate(commonName, caName), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-ca-certificate")
    public ResponseEntity<Object> createCACertificate(@RequestBody CertAuthorityDTO certAuth) {
        try {
            return new ResponseEntity<>(certificateService.createCACertificate(certAuth), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/certificate-signing-requests")
    public ResponseEntity<Object> getCertificateSigningRequests() {
        try {
            return new ResponseEntity<>(certificateService.getCertificateSigningRequests(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ca-certificates")
    public ResponseEntity<Object> getCACertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCACertificates(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/non-ca-certificates")
    public ResponseEntity<Object> getNonCACertificates() {
        try {
            return new ResponseEntity<>(certificateService.getNonCACertificates(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
