package timejts.Gateway;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class RestClientCertConfiguration {

    @Bean
    public RestTemplate restTemplate() throws Exception {

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

   /* @Bean
    public void setUpSSLContext() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);

        X509TrustManager defaultTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                defaultTm = (X509TrustManager) tm;
                break;
            }
        }

        FileInputStream myKeys = new FileInputStream("src/main/resources/static/keystore/truststore.jks");

        KeyStore myTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        myTrustStore.load(myKeys, "gatewaytruststorepass".toCharArray());

        myKeys.close();

        tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(myTrustStore);

        X509TrustManager myTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                myTm = (X509TrustManager) tm;
                break;
            }
        }

        final X509TrustManager finalDefaultTm = defaultTm;
        final X509TrustManager finalMyTm = myTm;
        X509TrustManager customTm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // If you're planning to use client-cert auth,
                // merge results from "defaultTm" and "myTm".
                //return finalDefaultTm.getAcceptedIssuers();
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                try {
                    finalMyTm.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    // This will throw another CertificateException if this fails too.
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                // If you're planning to use client-cert auth,
                // do the same as checking the server.
                //finalDefaultTm.checkClientTrusted(chain, authType);
            }
        };

        FileInputStream keystoreFile = new FileInputStream("src/main/resources/static/keystore/zull-keystore.jks");
        KeyStore myKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        myKeyStore.load(keystoreFile, "zuulpass".toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(myKeyStore, "zuulpass".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{customTm}, null);

        SSLContext.setDefault(sslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
    */

    /*@PostConstruct
    public void whenGETanHTTPSResource_thenCorrectResponse() throws Exception {
        ResponseEntity<ArrayList> response =
                restTemplate().getForEntity("https://localhost:8443/api/certificates", ArrayList.class);

        System.out.println(response.getBody());

    }*/
}
