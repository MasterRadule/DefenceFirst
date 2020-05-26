package timejts.PKI.services;

import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import timejts.PKI.dto.CACertificateCreationDTO;
import timejts.PKI.exceptions.InvalidCertificateDateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import static timejts.PKI.utils.Utilities.*;

@Service
public class PeriodicCertificateExpirationChecker {
    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.trust-store}")
    private String truststorePath;

    @Value("${server.ssl.trust-store-password}")
    private String truststorePassword;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CertificateService certificateService;

    private void checkNonCaCertificate(KeyStore ks, KeyStore truststore, String alias, X509Certificate certificate, Calendar now,
                                       Calendar other) throws KeyStoreException, CertificateEncodingException {
        other.setTime(certificate.getNotAfter());

        String email = new JcaX509CertificateHolder(certificate).getSubject().getRDNs(BCStyle.EmailAddress)[0]
                .getFirst().getValue().toString();

        if (other.before(now)) {
            // delete certificate from keystore and truststore and send email
            ks.deleteEntry(alias);
            truststore.deleteEntry(alias);
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

    private void checkCACertificate(X509Certificate certificate, Calendar now, Calendar other) throws CertificateException,
            OperatorCreationException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            IOException, InvalidCertificateDateException {

        other.setTime(certificate.getNotAfter());

        if (other.after(now) && other.get(Calendar.MONTH) - now.get(Calendar.MONTH) < 3) {
            // create new certificate with 2 years and 3 months expiration date
            CACertificateCreationDTO caDTO = new CACertificateCreationDTO(extractDataFromCertificate(certificate), null);
            certificateService.createCACertificate(caDTO);
        }
    }


    @Scheduled(cron = "${certificate.check.period}")
    private void checkCertificates() {
        try {
            // Load keystore
            KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

            // Load truststore
            KeyStore truststore = loadKeyStore(truststorePath, truststorePassword);

            Calendar now = new GregorianCalendar();
            now.setTime(new Date());

            Calendar other = new GregorianCalendar();

            Enumeration<String> enumeration = ks.aliases();
            X509Certificate certificate;
            String alias;
            while (enumeration.hasMoreElements()) {
                alias = enumeration.nextElement();
                certificate = (X509Certificate) ks.getCertificate(alias);

                if (ks.getCertificateChain(alias) != null) {
                    checkCACertificate(certificate, now, other);
                } else {
                    checkNonCaCertificate(ks, truststore, alias, certificate, now, other);
                }
            }

            saveKeyStore(ks, keystorePath, keystorePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
