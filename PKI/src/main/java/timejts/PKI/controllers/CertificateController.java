package timejts.PKI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timejts.PKI.exceptions.CertificateRevokedException;
import timejts.PKI.exceptions.NotExistingCertificateException;
import timejts.PKI.dto.SubjectDTO;
import timejts.PKI.services.CertificateService;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
    public ResponseEntity<Object> createNonCACertificate(@RequestParam String serialNumber,
                                                         @RequestParam String caSerialNumber) {
        try {
            return new ResponseEntity<>(certificateService
                    .createNonCACertificate(serialNumber, caSerialNumber), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/ca")
    public ResponseEntity<Object> createCACertificate(@RequestBody SubjectDTO certAuth) {
        try {
            return new ResponseEntity<>(certificateService.createCACertificate(certAuth), HttpStatus.CREATED);
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
    public ResponseEntity<Object> revokeCertificate(@RequestParam String serialNumber) {
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
}
