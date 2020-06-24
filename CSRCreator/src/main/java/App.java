import org.apache.http.ssl.SSLContextBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Scanner;

public class App {

    private static String commonNameRegExp = "^(?!.*\\s).*$";
    private static String organizationRegExp = "^[A-Z].*$";
    private static String organizationalUnitRegExp = "^[A-Z].*$";
    private static String cityRegExp = "^[A-Z](?!.*\\d).*$";
    private static String stateRegExp = "^[A-Z](?!.*\\d).*$";
    private static String countryRegExp = "^[A-Z]{2}$";
    private static String emailRegExp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static void main(String[] args) {
        System.out.println("Welcome to CSR creator");
        System.out.println();
        Scanner myObj = new Scanner(System.in);
        String commonName, organization, organizationalUnit, city, state, country, email;

        while (true) {
            System.out.print("Enter common name: ");
            commonName = myObj.nextLine();

            if (!commonName.matches(commonNameRegExp)) {
                System.out.println("Common name can not contain whitespaces!");
                System.out.println();
                continue;
            }

            System.out.print("Enter organization ");
            organization = myObj.nextLine();

            if (!organization.matches(organizationRegExp)) {
                System.out.println("Organization name must start with capital letter!");
                System.out.println();
                continue;
            }

            System.out.print("Enter organizational unit: ");
            organizationalUnit = myObj.nextLine();

            if (!organizationalUnit.matches(organizationalUnitRegExp)) {
                System.out.println("Organizational unit must start with capital letter!");
                System.out.println();
                continue;
            }

            System.out.print("Enter city: ");
            city = myObj.nextLine();

            if (!city.matches(cityRegExp)) {
                System.out.println("City must start with capital letter and can not contain numbers!");
                System.out.println();
                continue;
            }

            System.out.print("Enter state: ");
            state = myObj.nextLine();

            if (!state.matches(stateRegExp)) {
                System.out.println("State must start with capital letter and can not contain numbers!");
                System.out.println();
                continue;
            }

            System.out.print("Enter country: ");
            country = myObj.nextLine();

            if (!country.matches(countryRegExp)) {
                System.out.println("Country code must be two-letter word!");
                System.out.println();
                continue;
            }

            System.out.print("Enter email address: ");
            email = myObj.nextLine();

            if (!email.matches(emailRegExp)) {
                System.out.println("Invalid email address!");
                System.out.println();
                continue;
            }

            break;
        }

        try {
            createCSR(commonName, organization, organizationalUnit, city, state, country, email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createCSR(String commonName, String organization, String organizationalUnit, String city, String state, String country, String email) throws IOException, OperatorCreationException {
        KeyPairGenerator keyGen1 = null;
        try {
            keyGen1 = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen1.initialize(2048, new SecureRandom());
        KeyPair keypair1 = keyGen1.generateKeyPair();
        PublicKey publicKey1 = keypair1.getPublic();
        PrivateKey privateKey1 = keypair1.getPrivate();

        JcaPKCS8Generator pkcsGenerator = new JcaPKCS8Generator(privateKey1, null);
        PemObject pemObj = pkcsGenerator.generate();
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(pemObj);
        }

        // write PKCS8 to file
        String pkcs8Key = stringWriter.toString();
        FileOutputStream fos = new FileOutputStream("src/main/resources/private.key");
        fos.write(pkcs8Key.getBytes(StandardCharsets.UTF_8));
        fos.flush();
        fos.close();

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal("CN=" + commonName + ", OU=" + organizationalUnit + ", O=" + organization + ", C=" + country + ", L=" + city + "," +
                        " ST=" + state + ", EmailAddress=" + email), publicKey1);
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(privateKey1);
        PKCS10CertificationRequest csr = p10Builder.build(signer);

        sendCSR(csr);
    }

    private static void sendCSR(PKCS10CertificationRequest csr) {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL("https://localhost:8443/api/certificates/csr");
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream("src/main/resources/csr-keystore.jks"), "csrPass".toCharArray());
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(ks, "csrPass".toCharArray(), (map, socket) -> "csr")
                    .build();
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-java-serialized-object");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(csr.getEncoded().length));
            connection.setDoOutput(true);

            connection.getOutputStream().write(csr.getEncoded());
            //Send request
            /*DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.write(csr.getEncoded());
            wr.close();*/

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
