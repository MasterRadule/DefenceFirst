package timejts.PKI.services;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import timejts.PKI.dto.CertAuthorityDTO;

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
import java.util.Vector;

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
    private String root;

    public Object createNonCACertificate(String commonName, String caName) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, OperatorCreationException, ClassNotFoundException, InvalidKeyException {

        // Load certificate signing request and keystore
        String fileName = csrFolder + commonName + ".csr";
        byte[] csrData = FileUtils.readFileToByteArray(new File(fileName));
        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(csrData);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        // Get CA certificate and private key
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        Certificate cert = ks.getCertificate(caName);
        String caKeyPass = keyPassword + "-" + commonName;
        PrivateKey privKey = (PrivateKey) ks.getKey(commonName, caKeyPass.toCharArray());
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();

        // Generate serial number and set start/end date
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        Date dt = new Date();
        LocalDateTime endLocalDate = LocalDateTime.from(dt.toInstant()).plusYears(2);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, dt, endDate, csr
                .getSubject(), csr.getPublicKey());
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(false));
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true,
                new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.keyEncipherment));
        Vector<KeyPurposeId> extendedKeyUsages = new Vector<>();
        extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
        extendedKeyUsages.add(KeyPurposeId.id_kp_clientAuth);
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.37"), false, new ExtendedKeyUsage(extendedKeyUsages));
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new AuthorityKeyIdentifier((SubjectPublicKeyInfo) cert
                .getPublicKey()));
        X509CertificateHolder certHolder = certGen.build(contentSigner);

        // Convert to X509 certificate and save keystore and serial number
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate newCertificate = certConverter.getCertificate(certHolder);
        ks.setCertificateEntry(commonName, newCertificate);
        saveKeyStore(ks);
        saveSerialNumber(serialNumber);

        // send certificate on email address

        return null;
    }

    public Object createCACertificate(CertAuthorityDTO certAuth) throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, OperatorCreationException, CertificateException, ClassNotFoundException {

        // Get data about CA
        X500Name subjectCA = generateX500Name(certAuth);

        // Load keystore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        // Get root certificate and private key
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");
        Certificate cert = ks.getCertificate(root);
        PrivateKey privKey = (PrivateKey) ks.getKey(root, keyPassword.toCharArray());
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();

        // Generate CA public and private key
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();

        // Generate serial number and set start/end date
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        Date dt = new Date();
        LocalDateTime endLocalDate = LocalDateTime.from(dt.toInstant()).plusYears(3);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, dt, endDate, subjectCA, kp
                .getPublic());
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true,
                new X509KeyUsage(X509KeyUsage.keyCertSign | X509KeyUsage.digitalSignature | X509KeyUsage.keyEncipherment));
        Vector<KeyPurposeId> extendedKeyUsages = new Vector<>();
        extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
        extendedKeyUsages.add(KeyPurposeId.id_kp_clientAuth);
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.37"), false, new ExtendedKeyUsage(extendedKeyUsages));
        certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new AuthorityKeyIdentifier((SubjectPublicKeyInfo) cert
                .getPublicKey()));
        X509CertificateHolder certHolder = certGen.build(contentSigner);

        // Convert to X509 certificate
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");
        X509Certificate newCertificate = certConverter.getCertificate(certHolder);

        // Save certificate and private key in keystore and save serial number
        KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(kp.getPrivate(),
                new Certificate[]{newCertificate});
        String pass = keyPassword + "-" + certAuth.getCommonName();
        ks.setEntry(certAuth.getCommonName(), privKeyEntry, new KeyStore.PasswordProtection(pass.toCharArray()));
        saveKeyStore(ks);
        saveSerialNumber(serialNumber);

        return "CA Certificate " + certAuth.getCommonName() + " successfully created";
    }

    public String submitCSR(byte[] csrData) throws IOException {
        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(csrData);
        X500Name subjectName = csr.getSubject();
        RDN[] rdns = subjectName.getRDNs(BCStyle.CN);
        String commonName = IETFUtils.valueToString(rdns[0].getFirst().getValue());
        String fileName = csrFolder + commonName + ".csr";
        FileUtils.writeByteArrayToFile(new File(fileName), csrData);

        return "Certificate signing request successfully submitted";
    }

    private X500Name generateX500Name(CertAuthorityDTO certAuth) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certAuth.getCommonName());
        builder.addRDN(BCStyle.O, certAuth.getOrganization());
        builder.addRDN(BCStyle.OU, certAuth.getOrganizationalUnit());
        builder.addRDN(BCStyle.C, certAuth.getCountry());
        builder.addRDN(BCStyle.E, certAuth.getEmail());
        builder.addRDN(BCStyle.ST, certAuth.getCity() + ", " + certAuth.getState());

        return builder.build();
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

    private void saveKeyStore(KeyStore ks) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
            ks.store(fos, keystorePassword.toCharArray());
        }
    }
}
