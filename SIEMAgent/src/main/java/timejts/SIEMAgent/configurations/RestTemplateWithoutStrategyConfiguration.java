package timejts.SIEMAgent.configurations;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class RestTemplateWithoutStrategyConfiguration {

    @Bean("restTemplateWithoutStrategy")
    public RestTemplate restTemplateWithoutStrategy() throws Exception {
        System.out.println("Rest templejt");
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ResourceUtils
                        .getFile("src/main/resources/static/keystore/agent.jks"), "agentpass"
                        .toCharArray(), "agentpass".toCharArray())
                .loadTrustMaterial(ResourceUtils
                        .getFile("src/main/resources/static/keystore/agent-truststore.jks"), "agentpass"
                        .toCharArray())
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
