package timejts.PKI.services;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import timejts.PKI.dto.CertAuthorityDTO;
import timejts.PKI.dto.CertificateDTO;
import timejts.PKI.exceptions.*;
import timejts.PKI.exceptions.CertificateRevokedException;
import timejts.PKI.model.CertificateSigningRequest;
import timejts.PKI.model.RevokedCertificate;
import timejts.PKI.repository.CertificateSigningRequestRepository;
import timejts.PKI.repository.RevokedCertificatesRepository;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Optional;

import static timejts.PKI.utils.Utilities.*;

@Service
public class CertificateService {

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.key-alias}")
    private String root;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RevokedCertificatesRepository revokedCertificatesRepository;

    @Autowired
    private CertificateSigningRequestRepository csrRepository;


    public Object createNonCACertificate(String serialNumber, String caSerialNumber) throws KeyStoreException, IOException,
            CertificateException, CANotValidException, UnrecoverableEntryException, NoSuchAlgorithmException,
            OperatorCreationException, ValidCertificateAlreadyExistsException, CSRDoesNotExistException, InvalidKeyException, CACertificateDoesNotExistException {

        // Load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        // Check if subject already has valid certificate
        X509Certificate certificate = (X509Certificate) ks.getCertificate(serialNumber);
        if (certificate != null) {
            throw new ValidCertificateAlreadyExistsException("Certificate with given serial number already exists");
        }

        // Get CA certificate and private key and check validity of CA certificate
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        builder = builder.setProvider(bcp);
        certificate = (X509Certificate) ks.getCertificate(caSerialNumber);
        if (certificate == null) {
            throw new CACertificateDoesNotExistException("CA certificate with given serial number does not exist");
        }
        certificate.checkValidity();
        checkCAEndDate(certificate.getNotAfter());
        String caKeyPass = keystorePassword + caSerialNumber;
        PrivateKey privKey = (PrivateKey) ks.getKey(caSerialNumber, caKeyPass.toCharArray());

        // Build content signer and get issuer (CA)
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder(certificate).getSubject();

        // Load certificate signing request
        CertificateSigningRequest csr = csrRepository.findById(new BigInteger(serialNumber))
                .orElseThrow(() -> new CSRDoesNotExistException("Certificate signing request " +
                        "with given serial number does not exist"));
        JcaPKCS10CertificationRequest csrData = new JcaPKCS10CertificationRequest(csr.getCsr());

        // Generate serial number and set start/end date
        Date startDate = new Date();
        LocalDateTime endLocalDate = certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().minusMonths(3);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, new BigInteger(serialNumber), startDate,
                endDate, csrData.getSubject(), csrData.getPublicKey());
        X509CertificateHolder certHolder = addExtensionsAndBuildCertificate(certGen, certificate, contentSigner, false);

        // Convert to X509 certificate
        X509Certificate newCertificate = convertToX509Certificate(certHolder);

        // Save keystore and serial number
        ks.setCertificateEntry(serialNumber, newCertificate);
        saveKeyStore(ks, keystorePath, keystorePassword);

        // Create certificate file
        File certificateFile = x509CertificateToPem(certificate, serialNumber);

        // Send certificate on email address
        String email = csrData.getSubject().getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString();

        try {
            emailService.sendEmailWithCertificate(email, certificateFile);
        } catch (MessagingException ignored) {
        }

        csrRepository.delete(csr);

        return "Please check your email. Certificate is sent to your email address.";
    }

    public String createCACertificate(CertAuthorityDTO certAuth) throws KeyStoreException, IOException,
            UnrecoverableKeyException, NoSuchAlgorithmException, OperatorCreationException, CertificateException {

        // Get data about CA
        X500Name subjectCA = generateX500Name(certAuth);

        // Load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        // Get root certificate and private key
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        builder = builder.setProvider(bcp);
        X509Certificate cert = (X509Certificate) ks.getCertificate(root);
        PrivateKey privKey = (PrivateKey) ks.getKey(root, keystorePassword.toCharArray());

        // Build content signer and get issuer (root)
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder(cert).getSubject();

        // Generate CA public and private key
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();

        // Generate serial number and set start/end date
        BigInteger serialNumber = getSerialNumber();
        Date dt = new Date();
        LocalDateTime endLocalDate = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .plusYears(2).plusMonths(6);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, dt, endDate,
                subjectCA, kp.getPublic());
        X509CertificateHolder certHolder = addExtensionsAndBuildCertificate(certGen, cert,
                contentSigner, true);

        // Convert to X509 certificate
        X509Certificate newCertificate = convertToX509Certificate(certHolder);

        // Save certificate and private key in keystore and save serial number
        KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(kp.getPrivate(),
                new Certificate[]{newCertificate});
        String pass = keystorePassword + serialNumber;
        ks.setEntry(serialNumber.toString(), privKeyEntry, new KeyStore.PasswordProtection(pass.toCharArray()));
        saveKeyStore(ks, keystorePath, keystorePassword);

        return "CA Certificate for " + certAuth.getCommonName() + " successfully created";
    }

    public String submitCSR(byte[] csrData) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
            OperatorCreationException, PKCSException, DigitalSignatureInvalidException {

        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(csrData);
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        boolean signatureValid = csr.isSignatureValid(new JcaContentVerifierProviderBuilder()
                .setProvider(bcp).build(csr.getPublicKey()));
        if (!signatureValid)
            throw new DigitalSignatureInvalidException("Digital signature check failed");

        CertificateSigningRequest csrObj = new CertificateSigningRequest(null, csr.getEncoded());
        csrRepository.save(csrObj);

        return "Certificate signing request successfully submitted";
    }

    public ArrayList<CertAuthorityDTO> getCertificateSigningRequests() throws IOException {
        ArrayList<CertificateSigningRequest> csrs = (ArrayList<CertificateSigningRequest>) csrRepository.findAll();
        ArrayList<CertAuthorityDTO> certDTOs = new ArrayList<>();
        for (CertificateSigningRequest csr : csrs) {
            certDTOs.add(new CertAuthorityDTO(csr.getId(), new JcaPKCS10CertificationRequest(csr.getCsr())
                    .getSubject()));
        }

        return certDTOs;
    }

    public ArrayList<CertificateDTO> getCertificates() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);
        ArrayList<CertificateDTO> certificates = new ArrayList<>();

        Enumeration<String> enumeration = ks.aliases();
        while (enumeration.hasMoreElements()) {
            String alias = enumeration.nextElement();
            X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
            certificates.add(new CertificateDTO(certificate, alias, ks.getCertificateChain(alias) != null));
        }

        return certificates;
    }

    public String revokeCertificate(String serialNumber) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NotExistingCertificateException, CertificateAlreadyRevokedException {
        Optional<RevokedCertificate> r = revokedCertificatesRepository.findById(serialNumber);
        if (r.isPresent()) {
            throw new CertificateAlreadyRevokedException("Certificate with serial number " + serialNumber + " is already revoked");
        }

        // Load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        X509Certificate certificate = (X509Certificate) ks.getCertificate(serialNumber);
        if (certificate == null) {
            throw new NotExistingCertificateException("Certificate with serial number" + serialNumber + " doesn't exist");
        }

        saveRevokedCertificate(certificate);
        return "Certificate successfully revoked";
    }

    private void saveRevokedCertificate(X509Certificate certificate) throws CertificateNotYetValidException, CertificateExpiredException, CertificateEncodingException {
        certificate.checkValidity();
        String id = certificate.getSerialNumber().toString();
        String commonName = new JcaX509CertificateHolder(certificate).getSubject().getRDNs(BCStyle.CN)[0]
                .getFirst().getValue().toString();
        RevokedCertificate certificateToBeRevoked = new RevokedCertificate(id, commonName);
        revokedCertificatesRepository.save(certificateToBeRevoked);
    }

    private BigInteger getSerialNumber() {
        BigInteger serialNumber;
        Optional<CertificateSigningRequest> csr;

        do {
            serialNumber = new BigInteger(64, new SecureRandom());
            csr = csrRepository.findById(serialNumber);
        } while (csr.isPresent());

        return serialNumber;
    }

    public String validateCertificate(X509Certificate certificate) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NotExistingCertificateException, CertificateRevokedException {

        //load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        //check if certificate exsist in keystorage
        X509Certificate certificateFromKS = (X509Certificate) ks.getCertificate(certificate.getSerialNumber().toString());
        if(certificate == null) {
            throw new NotExistingCertificateException("Certificate doesn't exist");
        }

        //check certificate revoke status
        Optional<RevokedCertificate> r = revokedCertificatesRepository.findById(certificate.getSerialNumber().toString());
        if (r.isPresent()) {
            throw new CertificateRevokedException("Certificate is revoked");
        }

        //chain certificate -> root (dates and revoke status)
        //Certificate certificateCahin []  = ks.getCertificateChain(certificate.getIssuerX500Principal().);
        //for(Certificate c : certificateCahin){
       //     c.
      //  }


        //chaint root -> certificate (keys)

        return "Moze";
    }
}
