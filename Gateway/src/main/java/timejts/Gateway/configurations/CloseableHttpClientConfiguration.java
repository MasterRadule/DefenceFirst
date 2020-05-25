package timejts.Gateway.configurations;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
public class CloseableHttpClientConfiguration {

    @Bean
    public CloseableHttpClient closeableHttpClient() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder sslBuilder = SSLContexts.custom();
        sslBuilder.loadKeyMaterial(ResourceUtils
                .getFile("../Gateway/src/main/resources/static/keystore/zull-keystore.jks"), "zuulpass"
                .toCharArray(), "zuulpass".toCharArray());
        sslBuilder.loadTrustMaterial(ResourceUtils
                .getFile("../Gateway/src/main/resources/static/keystore/truststore.jks"), "gatewaytruststorepass"
                .toCharArray());

        SSLContext sslContext = sslBuilder.build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setSSLSocketFactory(socketFactory);

        return clientBuilder.build();
    }
}
