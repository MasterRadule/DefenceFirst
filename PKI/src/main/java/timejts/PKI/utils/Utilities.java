package timejts.PKI.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.BigIntegers;
import org.springframework.beans.factory.annotation.Value;
import timejts.PKI.dto.CertAuthorityDTO;
import timejts.PKI.exceptions.CANotValidException;
import timejts.PKI.model.RevokedCertificate;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class Utilities {

    @Value("${serial-numbers-file}")
    private static String serialNumbersFile;

    public static X500Name generateX500Name(CertAuthorityDTO certAuth) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certAuth.getCommonName());
        builder.addRDN(BCStyle.O, certAuth.getOrganization());
        builder.addRDN(BCStyle.OU, certAuth.getOrganizationalUnit());
        builder.addRDN(BCStyle.C, certAuth.getCountry());
        builder.addRDN(BCStyle.E, certAuth.getEmail());
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

    public static ArrayList<BigInteger> loadSerialNumbers() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(serialNumbersFile);
        ObjectInputStream ois = new ObjectInputStream(fis);

        ArrayList<BigInteger> serialNums = (ArrayList<BigInteger>) ois.readObject();

        ois.close();

        return serialNums;
    }

    public static BigInteger getSerialNumber() throws IOException, ClassNotFoundException {
        ArrayList<BigInteger> serialNumbers = loadSerialNumbers();
        BigInteger serialNumber;

        do {
            serialNumber = new BigInteger(64, new SecureRandom());
        } while (serialNumbers.contains(serialNumber));

        return serialNumber;
    }


    public static void saveSerialNumber(BigInteger newSerialNumber) throws IOException, ClassNotFoundException {
        ArrayList<BigInteger> serNumbers = loadSerialNumbers();
        serNumbers.add(newSerialNumber);
        FileOutputStream fos = new FileOutputStream(serialNumbersFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(serNumbers);
        oos.flush();
        oos.close();
    }

    public static void saveKeyStore(KeyStore ks, String path, String pass) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            ks.store(fos, pass.toCharArray());
        }
    }

}
