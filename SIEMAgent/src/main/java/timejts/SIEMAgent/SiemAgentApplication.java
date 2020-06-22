package timejts.SIEMAgent;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import timejts.SIEMAgent.configurations.ConfigProperties;

@SpringBootApplication
@EnableEncryptableProperties
@EnableEurekaClient
public class SiemAgentApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =  SpringApplication.run(SiemAgentApplication.class, args);
        ConfigProperties bean = context.getBean(ConfigProperties.class);
    }

}
