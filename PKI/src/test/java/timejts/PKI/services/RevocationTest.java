package timejts.PKI.services;

/*
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailServiceTests {
    @Autowired
    private EmailService emailService;

    @Test()
    void sendEmail_emailSent() {
        String to = "mikovic.nm@gmail.com";
        File certificate = new File("C:\\Users\\korisnik\\Desktop\\mikan.pem");
        assertDoesNotThrow(() -> emailService.sendEmail(to, certificate));
    }
}

 */

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import timejts.PKI.exceptions.CertificateAlreadyRevokedException;
import timejts.PKI.exceptions.NotExistingCertificateException;
import timejts.PKI.repository.RevokedCertificatesRepository;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
