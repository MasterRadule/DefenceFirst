package timejts.PKI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timejts.PKI.dto.CertAuthorityDTO;
import timejts.PKI.exceptions.CertificateAlreadyRevokedException;
import timejts.PKI.exceptions.NotExistingCertificateException;
import timejts.PKI.services.CertificateService;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
    public ResponseEntity<Object> createNonCACertificate(@RequestParam String commonName, @RequestParam String caName) {
        try {
            return new ResponseEntity<>(certificateService
                    .createNonCACertificate(commonName, caName), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/ca")
    public ResponseEntity<Object> createCACertificate(@RequestBody CertAuthorityDTO certAuth) {
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
    public ResponseEntity<Object> getCertificates(@RequestParam boolean ca) {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(ca), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/revoke")
    public ResponseEntity<Object> revokeCertificate(@RequestParam String commonName) {
        try {
            return new ResponseEntity<>(certificateService.revokeCertificate(commonName), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
