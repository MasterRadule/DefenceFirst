package timejts.PKI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;


    @Async
    public void sendEmailWithCertificate(String to, File certificate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(email);
        helper.setSubject("Certificate created");
        helper.setText("Your new certificate is in attachments");

        FileSystemResource certificateFile = new FileSystemResource(certificate);
        helper.addAttachment(Objects.requireNonNull(certificateFile.getFilename()), certificateFile);

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
