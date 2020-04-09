package timejts.PKI.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import timejts.PKI.exceptions.CertificateRevokedException;
import timejts.PKI.exceptions.CorruptedCertificateException;
import timejts.PKI.exceptions.NotExistingCertificateException;
import timejts.PKI.repository.RevokedCertificatesRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RevocationTest {
    @Autowired
    CertificateService certificateService;

    @Autowired
    RevokedCertificatesRepository repository;

    @Test()
    void revoke() {
        String commonName = "defencefirst";
        try {
            certificateService.revokeCertificate(commonName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkValid() {


        String serverCertFile = "C:\\Users\\Nemanja\\Desktop\\sertifikat.pem";
        CertificateFactory certFactory;
        FileInputStream inStream;
        try {
            certFactory = CertificateFactory
                    .getInstance("X.509");
            inStream = new FileInputStream(serverCertFile);
            X509Certificate cer = (X509Certificate) certFactory.generateCertificate(inStream);
            System.out.println(cer.getIssuerX500Principal().getName());
            String result = certificateService.validateCertificate(cer);
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
