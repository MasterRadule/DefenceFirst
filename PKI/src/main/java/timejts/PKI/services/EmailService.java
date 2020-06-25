package timejts.PKI.services;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;


    @Async
    public void sendEmailWithCertificateAndCAs(String to, File certificate, File caZipped) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(email);
        helper.setSubject("Certificate created");
        helper.setText("Your new certificate is in attachments");

        FileInputStream fis = new FileInputStream(certificate);
        helper.addAttachment(MimeUtility.encodeText(certificate.getName()),
                new ByteArrayResource(IOUtils.toByteArray(fis)));

        fis = new FileInputStream(caZipped);
        helper.addAttachment(MimeUtility.encodeText(caZipped.getName()), new ByteArrayResource(IOUtils
                .toByteArray(fis)));

        mailSender.send(message);
    }

    @Async
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(to);
        helper.setFrom(email);
        helper.setSubject(subject);
        helper.setText(body);

        mailSender.send(message);
    }

}
