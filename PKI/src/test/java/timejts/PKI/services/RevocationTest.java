package timejts.PKI.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import timejts.PKI.dto.CertificateDTO;
import timejts.PKI.repository.RevokedCertificatesRepository;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RevocationTest {
    @Autowired
    CertificateService certificateService;

    @Autowired
    RevokedCertificatesRepository repository;

    @Test()
    void revoke() {
        String commonName = "29264597646443857938939233571";
        String commonName2 = "29264597683337346086358336804";
        try {
            certificateService.revokeCertificate(commonName);
            certificateService.revokeCertificate(commonName2);
            List<CertificateDTO> list  = certificateService.getRevokedCertificates();
            System.out.println("123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void checkValid() {


        String serverCertFile = "src/test/resources/examples/sertifikat.pem";
        CertificateFactory certFactory;
        FileInputStream inStream;
        try {
            certFactory = CertificateFactory
                    .getInstance("X.509");
            inStream = new FileInputStream(serverCertFile);
            X509Certificate cer = (X509Certificate) certFactory.generateCertificate(inStream);
            System.out.println(cer.getIssuerX500Principal().getName());
            boolean result = certificateService.validateCertificate(cer);
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
