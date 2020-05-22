package timejts.Gateway.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import timejts.PKI.dto.CACertificateCreationDTO;
import timejts.PKI.dto.NonCACertificateCreationDTO;

import javax.validation.Valid;

@RestController
@RequestMapping("/certificates")
public class PKIController {

    @Autowired
    RestTemplate restTemplate;

    private String pkiURL = "https://localhost:8443/api/certificates";

    @DeleteMapping("/csr/{serialNumber}")
    public ResponseEntity<Object> rejectCSR(@PathVariable(value = "serialNumber") String serialNumber) {
        return restTemplate
                .exchange(String.format("%s/csr/%s", pkiURL, serialNumber), HttpMethod.DELETE, null, Object.class);
    }

    @GetMapping("/csr/{serialNumber}")
    public ResponseEntity<Object> getCSR(@PathVariable(value = "serialNumber") String serialNumber) {
        return restTemplate.getForEntity(String.format("%s/csr/%s", pkiURL, serialNumber), Object.class);
    }

    @PostMapping("/non-ca")
    public ResponseEntity<Object> createNonCACertificate(@Valid @RequestBody NonCACertificateCreationDTO creationDTO) {
        return restTemplate.postForEntity(String.format("%s/non-ca", pkiURL), creationDTO, Object.class);
    }

    @PostMapping("/ca")
    public ResponseEntity<Object> createCACertificate(@Valid @RequestBody CACertificateCreationDTO creationDTO) {
        return restTemplate.postForEntity(String.format("%s/ca", pkiURL), creationDTO, Object.class);
    }

    @GetMapping("/csr")
    public ResponseEntity<Object> getCertificateSigningRequests() {
        return restTemplate.getForEntity(String.format("%s/csr", pkiURL), Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getCertificates() {
        return restTemplate.getForEntity(pkiURL, Object.class);
    }

    @GetMapping("/ca")
    public ResponseEntity<Object> getCACertificates() {
        return restTemplate.getForEntity(String.format("%s/ca", pkiURL), Object.class);
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Object> getCertificate(@PathVariable(value = "serialNumber") String serialNumber) {
        return restTemplate.getForEntity(String.format("%s/%s", pkiURL, serialNumber), Object.class);
    }

    @PutMapping("/revoked")
    public ResponseEntity<Object> revokeCertificate(@RequestBody String serialNumber) {
        return restTemplate.exchange(String
                .format("%s/revoked", pkiURL), HttpMethod.PUT, new HttpEntity<>(serialNumber), Object.class);
    }

    @GetMapping("/revoked")
    public ResponseEntity<Object> getRevokedCertificates() {
        return restTemplate.getForEntity(String.format("%s/revoked", pkiURL), Object.class);
    }

    @GetMapping("/revoked/{serialNumber}")
    public ResponseEntity<Object> getCertificateStatus(@PathVariable(value = "serialNumber") String serialNumber) {
        return restTemplate.getForEntity(String.format("%s/revoked/%s", pkiURL, serialNumber), Object.class);
    }
}
