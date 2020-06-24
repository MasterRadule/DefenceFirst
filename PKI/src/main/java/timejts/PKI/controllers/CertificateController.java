package timejts.PKI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import timejts.PKI.dto.CACertificateCreationDTO;
import timejts.PKI.dto.NonCACertificateCreationDTO;
import timejts.PKI.services.CertificateService;

import javax.validation.Valid;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    CertificateService certificateService;

    @PostMapping("/csr")
    @Secured("ROLE_CSR")
    public ResponseEntity<Object> submitCSR(@RequestBody byte[] csrData) {
        try {
            return new ResponseEntity<>(certificateService.submitCSR(csrData), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/csr/{serialNumber}")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> rejectCSR(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.rejectCSR(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/csr/{serialNumber}")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCSR(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.getCertificateSigningRequest(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/non-ca")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> createNonCACertificate(@Valid @RequestBody NonCACertificateCreationDTO creationDTO) {
        try {
            return new ResponseEntity<>(certificateService.createNonCACertificate(creationDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/ca")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> createCACertificate(@Valid @RequestBody CACertificateCreationDTO creationDTO) {
        try {
            return new ResponseEntity<>(certificateService.createCACertificate(creationDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/csr")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCertificateSigningRequests() {
        try {
            return new ResponseEntity<>(certificateService.getCertificateSigningRequests(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ca")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCACertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{serialNumber}")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCertificate(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.getCertificate(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/revoked")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> revokeCertificate(@RequestBody String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.revokeCertificate(serialNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate")
    @Secured("ROLE_VALIDATOR")
    public ResponseEntity<Object> validateCertificate(@RequestBody byte[] certificate) {
        try {
            return new ResponseEntity<>(certificateService.validateCertificate(certificate), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/revoked")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getRevokedCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getRevokedCertificates(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/revoked/{serialNumber}")
    @Secured("ROLE_CLIENT")
    public ResponseEntity<Object> getCertificateStatus(@PathVariable(value = "serialNumber") String serialNumber) {
        try {
            return new ResponseEntity<>(certificateService.checkCertificateStatus(serialNumber, null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
