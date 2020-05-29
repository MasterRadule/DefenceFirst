package timejts.SIEMAgent;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HTTPSTest {

    @Test
    void TestCommunication() throws Exception {
        ResponseEntity<String> response =
                restTemplate().postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());
    }

    @Test
    void TestCommunicationFail() {
        RestTemplate rstTemplate = new RestTemplate();

        assertThrows(ResourceAccessException.class, () -> rstTemplate
                .postForEntity("https://localhost:8082/Test", "Test connection", String.class));
    }

    public TrustStrategy trustStrategy() {
        TrustStrategy trustStr = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                if (x509Certificates[0].getSerialNumber().toString().equals("23452da7")) {
                    System.out.println("PKI je");
                    return true;
                }
                Boolean response = false;
                try {
                    response = restTemplate()
                            .postForEntity("https://localhost:8443/api/certificates/validate", x509Certificates[0].getEncoded(), Boolean.class)
                            .getBody();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }
        };

        return trustStr;
    }

    public RestTemplate restTemplate() throws Exception {
        TrustStrategy trustStrategy = trustStrategy();

        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ResourceUtils
                        .getFile("src/main/resources/static/keystore/agent.jks"), "agentpass"
                        .toCharArray(), "agentpass".toCharArray())
                .loadTrustMaterial(ResourceUtils
                        .getFile("src/main/resources/static/keystore/agent-truststore.jks"), "agentpass"
                        .toCharArray(), trustStrategy)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
