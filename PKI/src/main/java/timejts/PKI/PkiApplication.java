package timejts.PKI;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableEncryptableProperties
@EnableAsync
@EnableScheduling
@EnableEurekaClient
public class PkiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PkiApplication.class, args);
    }
}
