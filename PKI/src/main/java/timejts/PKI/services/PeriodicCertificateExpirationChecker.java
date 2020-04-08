package timejts.PKI.services;

import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import static timejts.PKI.utils.Utilities.*;

@Service
public class PeriodicCertificateExpirationChecker {
    @Value("${non-ca-keystore}")
    private String nonCAKeystore;

    @Value("${non-ca-password}")
    private String nonCAPassword;

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CertificateService certificateService;


    @Scheduled(cron = "${certificate.check.period}")
    private void checkCertificates() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        // Load non CA keystore
        KeyStore ks = loadKeyStore(nonCAKeystore, nonCAPassword);

        Calendar now = new GregorianCalendar();
        now.setTime(new Date());

        Calendar other = new GregorianCalendar();

        Enumeration<String> enumeration = ks.aliases();
        X509Certificate certificate;
        String alias;
        String email;
        while (enumeration.hasMoreElements()) {
            alias = enumeration.nextElement();
            certificate = (X509Certificate) ks.getCertificate(alias);
            other.setTime(certificate.getNotAfter());

            email = new JcaX509CertificateHolder(certificate).getSubject().getRDNs(BCStyle.EmailAddress)[0]
                    .getFirst().getValue().toString();

            if (other.before(now)) {
                // delete certificate from keystore and send email
                ks.deleteEntry(alias);
                try {
                    emailService.sendEmail(email, "Certificate expired", String.format("Your certificate with serial " +
                            "number %s has expired", certificate.getSerialNumber()));
                } catch (MessagingException ignored) {
                }
            } else if (other.after(now) && other.get(Calendar.MONTH) - now.get(Calendar.MONTH) < 3) {
                // send reminder via email
                try {
                    emailService.sendEmail(email, "Certificate expires soon", String.format("Your certificate with " +
                            "serial number %s will expire in less than 3 months", certificate.getSerialNumber()));
                } catch (MessagingException ignored) {
                }
            }
        }

        saveKeyStore(ks, nonCAKeystore, nonCAPassword);

        // Load CA keystore
        ks = loadKeyStore(keystorePath, keystorePassword);

        enumeration = ks.aliases();
        while (enumeration.hasMoreElements()) {
            certificate = (X509Certificate) ks.getCertificate(enumeration.nextElement());
            other.setTime(certificate.getNotAfter());

            if (other.after(now) && other.get(Calendar.MONTH) - now.get(Calendar.MONTH) < 3) {
                try {
                    // create new certificate with 2 years and 3 months expiration date
                    certificateService.createCACertificate(extractDataFromCertificate(certificate));
                } catch (UnrecoverableKeyException | OperatorCreationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
