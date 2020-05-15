package timejts.SIEMAgent;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HTTPSTest {

    @Test
    public void TestCommunication() throws Exception {
        ResponseEntity<String> response =
                restTemplate().postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());

    }

    @Test
    public void TestCommunicationFail() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        assertThrows(ResourceAccessException.class, () -> {
            ResponseEntity<String> response =
                    restTemplate.postForEntity("https://localhost:8082/Test", "Test connection", String.class);
        });

    }

    public RestTemplate restTemplate() throws Exception {

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
