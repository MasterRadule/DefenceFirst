package timejts.PKI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.io.File;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;


    @Async
    public void sendEmail(String to, String name, File certificate) {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.setSubject("Certificate");
            mimeMessage.setText("Your new certificates is in attachments");

            FileSystemResource certificateFile = new FileSystemResource(certificate);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.addAttachment(String.format("%s.pem", name), certificateFile);
        };

        try {
            mailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

}
