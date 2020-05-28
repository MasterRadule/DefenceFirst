package timejts.SIEMAgent.configurations;

import org.apache.http.conn.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TrustStrategyConfiguration {

    @Autowired
    @Qualifier("restTemplateWithoutStrategy")
    RestTemplate restTemplate;

    @Bean
    public TrustStrategy trustStrategy() {
        return (x509Certificates, s) -> (Boolean) restTemplate
                .postForEntity("https://localhost:8443/api/certificates/validate", x509Certificates[0]
                        .getEncoded(), Object.class)
                .getBody();
    }
}
