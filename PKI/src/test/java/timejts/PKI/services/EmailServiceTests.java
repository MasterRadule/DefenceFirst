package timejts.PKI.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailServiceTests {
    @Autowired
    private EmailService emailService;

    @Test()
    void sendEmail_emailSent() {
        String to = "mikovic.nm@gmail.com";
        File certificate = new File("C:\\Users\\korisnik\\Desktop\\mikan.pem");
        assertDoesNotThrow(() -> emailService.sendEmailWithCertificateAndCAs(to, certificate));
    }
}
