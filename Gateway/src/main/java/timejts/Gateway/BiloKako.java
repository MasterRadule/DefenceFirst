package timejts.Gateway;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BiloKako {

    @PostConstruct
    public void metodica(){
        System.out.println("*****************************");
        System.out.println(System.getProperty("javax.net.ssl.keyStore"));
        System.out.println(System.getProperty("java.net.ssl.keyStorePassword"));
        System.out.println("#############################");
        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        System.out.println(System.getProperty("java.net.ssl.trustStorePassword"));
        System.out.println("*****************************");

    }
}
