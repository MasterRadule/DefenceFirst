package timejts.PKI;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class PkiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PkiApplication.class, args);
    }

}
