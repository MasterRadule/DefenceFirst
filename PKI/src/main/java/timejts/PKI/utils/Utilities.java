package timejts.PKI.utils;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Value;
import timejts.PKI.dto.SubjectDTO;
import timejts.PKI.exceptions.CANotValidException;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import static org.bouncycastle.asn1.x509.X509Extensions.IssuerAlternativeName;
import static org.bouncycastle.asn1.x509.X509Extensions.SubjectAlternativeName;

public class Utilities {

    @Value("${serial-numbers-file}")
    private static String serialNumbersFile;

    public static X500Name generateX500Name(SubjectDTO certAuth) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certAuth.getCommonName());
        builder.addRDN(BCStyle.O, certAuth.getOrganization());
        builder.addRDN(BCStyle.OU, certAuth.getOrganizationalUnit());
        builder.addRDN(BCStyle.C, certAuth.getCountry());
        builder.addRDN(BCStyle.EmailAddress, certAuth.getEmail());
        builder.addRDN(BCStyle.L, certAuth.getCity());
        builder.addRDN(BCStyle.ST, certAuth.getState());

        return builder.build();
    }

    public static void checkCAEndDate(Date notAfter) throws CANotValidException {
        LocalDate todayDate = Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(notAfter.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        long monthsBetween = ChronoUnit.MONTHS.between(todayDate, endDate);
        if (monthsBetween <= 3)
            throw new CANotValidException("CA certificate will have been invalid in less than 3 months");
    }

    public static File x509CertificateToPem(X509Certificate cert, String commonName) throws IOException {
        File f = new File(commonName + ".pem");
        FileWriter fileWriter = new FileWriter(f);
        JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        return f;
    }

    public static void addBasicExtensions(X509v3CertificateBuilder certGen, boolean ca) throws CertIOException {
        // Basic constraints
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(ca));

        // Key Usage
        int keyUsage = X509KeyUsage.digitalSignature | X509KeyUsage.keyEncipherment;
        if (ca)
            keyUsage = keyUsage | X509KeyUsage.keyCertSign;
        certGen.addExtension(X509Extensions.KeyUsage, true, new X509KeyUsage(keyUsage));

        // Extended Key Usage
        Vector<KeyPurposeId> extendedKeyUsages = new Vector<>();
        extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
        extendedKeyUsages.add(KeyPurposeId.id_kp_clientAuth);
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, false,
                new ExtendedKeyUsage(extendedKeyUsages));
    }

    public static void addAlternativeNamesExtensions(X509v3CertificateBuilder certGen, String subjEmail, String issuerEmail)
            throws CertIOException {
        ArrayList<GeneralName> generalNames = new ArrayList<>();
        generalNames.add(new GeneralName(GeneralName.rfc822Name, subjEmail));
        generalNames.add(new GeneralName(GeneralName.iPAddress, "127.0.0.1"));
        GeneralNames altNamesSeq = GeneralNames
                .getInstance(new DERSequence(generalNames.toArray(new GeneralName[]{})));
        certGen.addExtension(SubjectAlternativeName, false, altNamesSeq);

        generalNames.clear();
        generalNames.add(new GeneralName(GeneralName.rfc822Name, issuerEmail));
        generalNames.add(new GeneralName(GeneralName.iPAddress, "127.0.0.1"));
        altNamesSeq = GeneralNames
                .getInstance(new DERSequence(generalNames.toArray(new GeneralName[]{})));
        certGen.addExtension(IssuerAlternativeName, false, altNamesSeq);
    }

    public static void addKeyIdentifierExtensions(X509v3CertificateBuilder certGen, PublicKey subjKey, PublicKey issuerKey)
            throws IOException, NoSuchAlgorithmException {
        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, extensionUtils
                .createSubjectKeyIdentifier(subjKey));
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, extensionUtils
                .createAuthorityKeyIdentifier(issuerKey));
    }

    public static X509Certificate convertToX509Certificate(X509CertificateHolder certHolder) throws CertificateException {
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        certConverter = certConverter.setProvider(bcp);

        return certConverter.getCertificate(certHolder);
    }

    public static KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        return ks;
    }

    public static void saveKeyStore(KeyStore ks, String path, String pass) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            ks.store(fos, pass.toCharArray());
        }
    }

    public static SubjectDTO extractDataFromCertificate(X509Certificate certificate) throws CertificateEncodingException {
        SubjectDTO dto = new SubjectDTO();
        dto.setSerialNumber(null);

        JcaX509CertificateHolder holder = new JcaX509CertificateHolder(certificate);

        dto.setEmail(holder.getSubject().getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString());
        dto.setCity(holder.getSubject().getRDNs(BCStyle.L)[0].getFirst().getValue().toString());
        dto.setCommonName(holder.getSubject().getRDNs(BCStyle.CN)[0].getFirst().getValue().toString());
        dto.setCountry(holder.getSubject().getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
        dto.setOrganization(holder.getSubject().getRDNs(BCStyle.O)[0].getFirst().getValue().toString());
        dto.setOrganizationalUnit(holder.getSubject().getRDNs(BCStyle.OU)[0].getFirst().getValue().toString());

        return dto;
    }

    public static String getCommonName(X509Certificate certificate) throws CertificateEncodingException {
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];

        return cn.getFirst().getValue().toString();
    }

    public static String getIssuerCommonName(X509Certificate certificate) {
        String issuerData = certificate.getIssuerX500Principal().getName();
        String[] parts = issuerData.split("CN=");
        int index = parts[1].indexOf(',') == -1 ? parts[1].length() : parts[1].indexOf(',');

        return parts[1].substring(0, index);
    }

    public static X500Name createSubjectX500Name(X500Name subj, String caSerialNumber) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, subj.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.O, subj.getRDNs(BCStyle.O)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.OU, subj.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.C, subj.getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.EmailAddress, subj.getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.L, subj.getRDNs(BCStyle.L)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.ST, subj.getRDNs(BCStyle.ST)[0].getFirst().getValue().toString());
        builder.addRDN(BCStyle.SERIALNUMBER, caSerialNumber);

        return builder.build();
    }
}
