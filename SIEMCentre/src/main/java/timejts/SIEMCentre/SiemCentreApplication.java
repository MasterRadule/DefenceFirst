package timejts.SIEMCentre;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "timejts.SIEMCentre.repository", "timejts.SIEMCentre.configurations",
        "timejts.SIEMCentre.controllers", "timejts.SIEMCentre.services"})
@EnableEurekaClient
@EnableEncryptableProperties
@EnableScheduling
public class SiemCentreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiemCentreApplication.class, args);
    }

}
