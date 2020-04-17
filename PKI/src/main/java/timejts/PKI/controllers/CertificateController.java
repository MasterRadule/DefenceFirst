package timejts.PKI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timejts.PKI.dto.CACertificateCreationDTO;
import timejts.PKI.dto.NonCACertificateCreationDTO;
import timejts.PKI.services.CertificateService;

import java.security.cert.X509Certificate;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    CertificateService certificateService;

    @PostMapping("/csr")
    public ResponseEntity<Object> submitCSR(@RequestBody byte[] csrData) {
        try {
            return new ResponseEntity<>(certificateService.submitCSR(csrData), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/non-ca")
    public ResponseEntity<Object> createNonCACertificate(@RequestBody NonCACertificateCreationDTO creationDTO) {
        try {
            return new ResponseEntity<>(certificateService.createNonCACertificate(creationDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/ca")
    public ResponseEntity<Object> createCACertificate(@RequestBody CACertificateCreationDTO creationDTO) {
        try {
            return new ResponseEntity<>(certificateService.createCACertificate(creationDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/csr")
    public ResponseEntity<Object> getCertificateSigningRequests() {
        try {
            return new ResponseEntity<>(certificateService.getCertificateSigningRequests(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Object> getCertificate(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.getCertificate(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/revoked")
    public ResponseEntity<Object> revokeCertificate(@RequestBody String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.revokeCertificate(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validateCertificate(@RequestBody X509Certificate certificate) {
        try {
            return new ResponseEntity<>(certificateService.validateCertificate(certificate), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/revoked")
    public ResponseEntity<Object> getRevokedCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getRevokedCertificates(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/revoked/{serialNumber}")
    public ResponseEntity<Object> getCertificateStatus(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.checkCertificateStatus(serialNumber, null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
