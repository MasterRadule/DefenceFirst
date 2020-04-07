package timejts.PKI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import static timejts.PKI.utils.Utilities.loadKeyStore;

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

    @Scheduled(cron = "${certificate.check.period}")
    private void checkCertificates() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        // Load non CA keystore
        KeyStore ks = loadKeyStore(nonCAKeystore, nonCAPassword);

        Calendar now = new GregorianCalendar();
        now.setTime(new Date());

        Calendar other = new GregorianCalendar();

        Enumeration<String> enumeration = ks.aliases();
        X509Certificate certificate;
        while (enumeration.hasMoreElements()) {
            certificate = (X509Certificate) ks.getCertificate(enumeration.nextElement());
            other.setTime(certificate.getNotAfter());

            if (other.before(now)) {
                // delete certificate from keystore and send email
            } else if (other.after(now) && other.get(Calendar.MONTH) - now.get(Calendar.MONTH) < 3) {
                // send email
            }
        }

        // Load CA keystore
        ks = loadKeyStore(keystorePath, keystorePassword);

        enumeration = ks.aliases();
        while (enumeration.hasMoreElements()) {
            certificate = (X509Certificate) ks.getCertificate(enumeration.nextElement());
            other.setTime(certificate.getNotAfter());

            if (other.after(now) && other.get(Calendar.MONTH) - now.get(Calendar.MONTH) < 3) {
                other.add(Calendar.YEAR, 2);
                other.add(Calendar.MONTH, 3);
                // create new certificate with 2 years and 3 months expiration date and send email
            }
        }
    }

}
