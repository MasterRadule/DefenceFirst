package timejts.PKI.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import timejts.PKI.repository.RevokedCertificatesRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RevocationTest {
    @Autowired CertificateService certificateService;

    @Autowired
    RevokedCertificatesRepository repository;

    @Test()
    void revoke(){
        String commonName  = "defencefirst";
        try {
            certificateService.revokeCertificate(commonName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
