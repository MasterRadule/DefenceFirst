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

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;


    @Async
    public void sendEmail(String to, File certificate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(email);
        helper.setSubject("Certificate");
        helper.setText("Your new certificate is in attachments");

        FileSystemResource certificateFile = new FileSystemResource(certificate);
        helper.addAttachment(certificateFile.getFilename(), certificateFile);

        mailSender.send(message);
    }

}
