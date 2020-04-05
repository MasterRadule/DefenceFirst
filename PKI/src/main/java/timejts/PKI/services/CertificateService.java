package timejts.PKI.services;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@Service
public class CertificateService {

    private static String csrFolder = "src/main/resources/static/csr/";

    private static String serialNumbers = "src/main/resources/static/keystore/serialNumbers.sn";

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.key-password}")
    private String keyPassword;

    @Value("${server.ssl.key-alias}")
    private String alias;

    public Object createNonCACertificate(String commonName, String caName) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, SignatureException, UnrecoverableKeyException, OperatorCreationException, ClassNotFoundException {
        String fileName = csrFolder + commonName + ".csr";
        byte[] csrData = FileUtils.readFileToByteArray(new File(fileName));
        PKCS10 csr = new PKCS10(csrData);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        Certificate cert = ks.getCertificate(caName);
        PrivateKey privKey = (PrivateKey) ks.getKey(alias, keyPassword.toCharArray());
        ContentSigner contentSigner = builder.build(privKey);
        org.bouncycastle.asn1.x500.X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert)
                .getSubject();

        BigInteger serialNumber = generateSerialNumber();
        Date dt = new Date();
        LocalDateTime endLocalDate = LocalDateTime.from(dt.toInstant()).plusYears(2);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, dt, endDate, org.bouncycastle.asn1.x500.X500Name
                .getInstance(csr
                        .getSubjectName()), csr.getSubjectPublicKeyInfo());

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate newCertificate = certConverter.getCertificate(certHolder);
        saveSerialNumber(serialNumber);

        return null;
    }

    public String submitCSR(byte[] csrData) throws NoSuchAlgorithmException, SignatureException, IOException {
        PKCS10 csr = new PKCS10(csrData);
        X500Name subjectName = csr.getSubjectName();
        String fileName = csrFolder + subjectName.getCommonName() + ".csr";
        FileUtils.writeByteArrayToFile(new File(fileName), csrData);

        return "Certificate signing request successfully submitted";
    }

    private BigInteger generateSerialNumber() throws IOException, ClassNotFoundException {
        ArrayList<BigInteger> serialNumbersList = loadSerialNumbers();
        Random rand = new Random();
        boolean exist = true;
        StringBuilder serialNumber;
        BigInteger newSerNum = null;

        while (exist) {
            serialNumber = new StringBuilder();
            for (int i = 0; i < 36; i++) {
                serialNumber.append(rand.nextInt(10));
            }
            newSerNum = new BigInteger(serialNumber.toString());
            if (!serialNumbersList.contains(newSerNum))
                exist = false;
        }

        return newSerNum;
    }

    private ArrayList<BigInteger> loadSerialNumbers() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(serialNumbers);
        ObjectInputStream ois = new ObjectInputStream(fis);

        ArrayList<BigInteger> serialNums = (ArrayList<BigInteger>) ois.readObject();

        ois.close();

        return serialNums;
    }

    private void saveSerialNumber(BigInteger newSerialNumber) throws IOException, ClassNotFoundException {
        ArrayList<BigInteger> serNumbers = loadSerialNumbers();
        serNumbers.add(newSerialNumber);
        FileOutputStream fos = new FileOutputStream(serialNumbers);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(serNumbers);
        oos.close();
    }
}
