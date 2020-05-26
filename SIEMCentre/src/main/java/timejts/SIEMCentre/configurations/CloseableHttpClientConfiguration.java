package timejts.SIEMCentre.configurations;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    TrustStrategy trustStrategy;

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.trust-store}")
    private String truststorePath;

    @Value("${server.ssl.trust-store-password}")
    private String truststorePassword;

    @Bean
    public CloseableHttpClient closeableHttpClient() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder sslBuilder = SSLContexts.custom();
        sslBuilder.loadKeyMaterial(ResourceUtils
                .getFile(keystorePath), keystorePassword
                .toCharArray(), keystorePassword.toCharArray());
        sslBuilder.loadTrustMaterial(ResourceUtils
                .getFile(truststorePath), truststorePassword
                .toCharArray(), trustStrategy);

        SSLContext sslContext = sslBuilder.build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setSSLSocketFactory(socketFactory);

        return clientBuilder.build();
    }
}
