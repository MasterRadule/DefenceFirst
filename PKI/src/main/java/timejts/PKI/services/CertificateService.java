package timejts.PKI.services;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

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

    public Object createNonCACertificate(String commonName, String caName) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, SignatureException, UnrecoverableKeyException, OperatorCreationException, ClassNotFoundException, InvalidKeyException {
        String fileName = csrFolder + commonName + ".csr";
        byte[] csrData = FileUtils.readFileToByteArray(new File(fileName));
        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(csrData);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        Certificate cert = ks.getCertificate(caName);
        PrivateKey privKey = (PrivateKey) ks.getKey(alias, keyPassword.toCharArray());
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert)
                .getSubject();

        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        Date dt = new Date();
        LocalDateTime endLocalDate = LocalDateTime.from(dt.toInstant()).plusYears(2);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, dt, endDate, csr.getSubject(), csr.getPublicKey());

        X509CertificateHolder certHolder = certGen.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate newCertificate = certConverter.getCertificate(certHolder);
        saveSerialNumber(serialNumber);

        return null;
    }

    public String submitCSR(byte[] csrData) throws NoSuchAlgorithmException, SignatureException, IOException {
        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(csrData);
        X500Name subjectName = csr.getSubject();
        RDN[] rdns = subjectName.getRDNs(BCStyle.CN);
        String commonName = IETFUtils.valueToString(rdns[0].getFirst().getValue());
        String fileName = csrFolder + commonName + ".csr";
        FileUtils.writeByteArrayToFile(new File(fileName), csrData);

        return "Certificate signing request successfully submitted";
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
