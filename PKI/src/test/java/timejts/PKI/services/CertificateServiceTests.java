package timejts.PKI.services;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import timejts.PKI.dto.CreationDataDTO;
import timejts.PKI.dto.NonCACertificateCreationDTO;
import timejts.PKI.dto.SubjectDTO;
import timejts.PKI.exceptions.*;
import timejts.PKI.utils.Utilities;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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

        /*SubjectDTO caDTO1 = new SubjectDTO(null, "Asia Chamber",
                "Asia DefenceFirst", "Beijing corp.",
                "Beijing", "Beijing", "CN", "master.daca09@gmail.com");
        CACertificateCreationDTO caCreationDTO = new CACertificateCreationDTO(caDTO1, null);
        String ca1 = certificateService.createCACertificate(caCreationDTO);
        System.out.println(ca1);*/

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

        JcaPKCS8Generator pkcsGenerator = new JcaPKCS8Generator(privateKey1, null);
        PemObject pemObj = pkcsGenerator.generate();
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(pemObj);
        }

        // write PKCS8 to file
        String pkcs8Key = stringWriter.toString();
        FileOutputStream fos = new FileOutputStream("centre.key");
        fos.write(pkcs8Key.getBytes(StandardCharsets.UTF_8));
        fos.flush();
        fos.close();

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal("CN=SIEMCentre1, OU=Beijing corp., O=Asia DefenceFirst, C=CN, L=Beijing," +
                        " ST=Beijing, EmailAddress=master.daca09@gmail.com"), publicKey1);
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(privateKey1);
        PKCS10CertificationRequest csr = p10Builder.build(signer);
        certificateService.submitCSR(csr.getEncoded());

        /*KeyPairGenerator keyGen2 = null;
        try {
            keyGen2 = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen2.initialize(2048, new SecureRandom());
        KeyPair keypair2 = keyGen2.generateKeyPair();
        PublicKey publicKey2 = keypair2.getPublic();
        PrivateKey privateKey2 = keypair2.getPrivate();

        JcaPKCS8Generator pkcsGenerator2 = new JcaPKCS8Generator(privateKey2, null);
        PemObject pemObj2 = pkcsGenerator2.generate();
        StringWriter stringWriter2 = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter2)) {
            pemWriter.writeObject(pemObj2);
        }

        // write PKCS8 to file
        String pkcs8Key2 = stringWriter2.toString();
        FileOutputStream fos2 = new FileOutputStream("centre.key");
        fos2.write(pkcs8Key2.getBytes(StandardCharsets.UTF_8));
        fos2.flush();
        fos2.close();

        PKCS10CertificationRequestBuilder p10Builder2 = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal("CN=SIEMCentre1, OU=Beijing corp., O=Asia DefenceFirst, C=CN, L=Beijing," +
                        " ST=Beijing, EmailAddress=master.daca09@gmail.com"), publicKey2);
        JcaContentSignerBuilder csBuilder2 = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer2 = csBuilder2.build(privateKey2);
        PKCS10CertificationRequest csr2 = p10Builder2.build(signer2);
        certificateService.submitCSR(csr2.getEncoded());*/

        ArrayList<SubjectDTO> caDTO = certificateService.getCertificateSigningRequests();
        LocalDateTime startLocalDate = new Date().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMinutes(1);
        Date startDate = Date.from(startLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime endLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMonths(24);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        CreationDataDTO cdd = new CreationDataDTO("sha256WithRSAEncryption", startDate, endDate, true);
        NonCACertificateCreationDTO nonCADTO = new NonCACertificateCreationDTO(caDTO.get(0)
                .getSerialNumber(), "15797428220941440972", cdd);
        certificateService.createNonCACertificate(nonCADTO);
    }

    @Test
    void validateCertificateCorrectCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateRevokedException, CorruptedCertificateException, NotExistingCertificateException, SignatureException, NoSuchProviderException, InvalidKeyException {
        KeyStore ks = Utilities.loadKeyStore(keystorePath, keystorePassword);
        X509Certificate cert = (X509Certificate) ks.getCertificate("29265996049563850812855439007");
        boolean result = certificateService.validateCertificate(cert.getEncoded());
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
            boolean result = certificateService.validateCertificate(cer.getEncoded());
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
