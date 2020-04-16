package timejts.PKI.services;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import timejts.PKI.dto.CACertificateCreationDTO;
import timejts.PKI.dto.SubjectDTO;
import timejts.PKI.exceptions.*;
import timejts.PKI.utils.Utilities;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CertificateServiceTests {

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Autowired
    private CertificateService certificateService;

    @Test
    void generateCertificate() throws OperatorCreationException, UnrecoverableEntryException, NoSuchAlgorithmException,
            KeyStoreException, CertificateException, IOException, DigitalSignatureInvalidException, InvalidKeyException,
            PKCSException, CSRDoesNotExistException, CANotValidException, CACertificateDoesNotExistException,
            ValidCertificateAlreadyExistsException, InvalidCertificateDateException {

        SubjectDTO caDTO1 = new SubjectDTO(null, "Asia Chamber",
                "Asia DefenceFirst", "Beijing corp.",
                "Beijing", "Beijing", "CN", "master.daca09@gmail.com");
        CACertificateCreationDTO caCreationDTO = new CACertificateCreationDTO(caDTO1, null);
        String ca1 = certificateService.createCACertificate(caCreationDTO);

        KeyPairGenerator keyGen1 = null;
        try {
            keyGen1 = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen1.initialize(2048, new SecureRandom());
        KeyPair keypair1 = keyGen1.generateKeyPair();
        PublicKey publicKey1 = keypair1.getPublic();
        PrivateKey privateKey1 = keypair1.getPrivate();
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal("CN=Mujo Alen, OU=Beijing corp., O=Asia DefenceFirst, C=CN, L=Beijing," +
                        " ST=Beijing, EmailAddress=mujoalen@gmail.com"), publicKey1);
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(privateKey1);
        PKCS10CertificationRequest csr = p10Builder.build(signer);
        certificateService.submitCSR(csr.getEncoded());

        Date dt = new Date();
        LocalDateTime startLocalDate = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(6);
        Date startDate = Date.from(startLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        ArrayList<SubjectDTO> caDTO = certificateService.getCertificateSigningRequests();
        // String nonCA1 = certificateService.createNonCACertificate(caDTO.get(0).getSerialNumber(), ca1, dt, true).toString();
        // String nonCA2 = certificateService.createNonCACertificate(caDTO.get(0).getSerialNumber(), ca1, startDate, false).toString();

        // System.out.println(nonCA1);
        // System.out.println(nonCA2);
    }

    @Test
    void validateCertificateCorrectCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateRevokedException, CorruptedCertificateException, NotExistingCertificateException, SignatureException, NoSuchProviderException, InvalidKeyException {
        KeyStore ks = Utilities.loadKeyStore(keystorePath, keystorePassword);
        X509Certificate cert = (X509Certificate) ks.getCertificate("29265996049563850812855439007");
        boolean result = certificateService.validateCertificate(cert);
    }

    @Test
    void validaCertificateCorruptedCertificate() {
        String serverCertFile = "src/test/resources/examples/29265996049563850812855439007.pem";
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
