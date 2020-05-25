package timejts.Gateway.configurations;

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
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate getRestTemplate() throws Exception {

        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ResourceUtils
                        .getFile("../Gateway/src/main/resources/static/keystore/zull-keystore.jks"), "zuulpass"
                        .toCharArray(), "zuulpass".toCharArray())
                .loadTrustMaterial(ResourceUtils
                        .getFile("../Gateway/src/main/resources/static/keystore/truststore.jks"), "gatewaytruststorepass"
                        .toCharArray())
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }


    /*@PostConstruct
    public void whenGETanHTTPSResource_thenCorrectResponse() throws Exception {
        ResponseEntity<ArrayList> response =
                restTemplate().getForEntity("https://localhost:8443/api/certificates", ArrayList.class);

        System.out.println(response.getBody());

    }*/
}
