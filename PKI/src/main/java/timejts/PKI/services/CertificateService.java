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
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import timejts.PKI.dto.CertAuthorityDTO;
import timejts.PKI.dto.CertificateDTO;
import timejts.PKI.exceptions.*;
import timejts.PKI.model.CertificateSigningRequest;
import timejts.PKI.model.RevokedCertificate;
import timejts.PKI.repository.CertificateSigningRequestRepository;
import timejts.PKI.repository.RevokedCertificatesRepository;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileOutputStream;
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

    @Value("${non-ca-keystore}")
    private String nonCAKeystore;

    @Value("${non-ca-password}")
    private String nonCAPassword;

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


    public Object createNonCACertificate(String serialNumber, String commonName, String caSerialNumber) throws KeyStoreException, IOException,
            CertificateException, CANotValidException, UnrecoverableEntryException, NoSuchAlgorithmException,
            OperatorCreationException, ValidCertificateAlreadyExistsException, CSRDoesNotExistException, InvalidKeyException {

        // Load non CA keystore
        KeyStore nonCAKS = loadKeyStore(nonCAKeystore, nonCAPassword);

        // Check if subject already has valid certificate
        X509Certificate subjCert = (X509Certificate) nonCAKS.getCertificate(serialNumber);
        if (subjCert != null) {
            throw new ValidCertificateAlreadyExistsException("Certificate with given serial number already exists");
        }

        // Load CA keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        // Get CA certificate and private key and check validity of CA certificate
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        builder = builder.setProvider(bcp);
        X509Certificate cert = (X509Certificate) ks.getCertificate(caSerialNumber);
        cert.checkValidity();
        checkCAEndDate(cert.getNotAfter());
        String caKeyPass = keystorePassword + caSerialNumber;
        PrivateKey privKey = (PrivateKey) ks.getKey(serialNumber, caKeyPass.toCharArray());

        // Build content signer and get issuer (CA)
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder(cert).getSubject();

        // Load certificate signing request
        CertificateSigningRequest csr = csrRepository.findById(new BigInteger(serialNumber))
                .orElseThrow(() -> new CSRDoesNotExistException("Certificate signing request " +
                        "with given serial number does not exist"));
        JcaPKCS10CertificationRequest csrData = new JcaPKCS10CertificationRequest(csr.getCsr());

        // Generate serial number and set start/end date
        Date startDate = new Date();
        LocalDateTime endLocalDate = LocalDateTime.from(cert.getNotAfter().toInstant()).minusMonths(3);
        Date endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, new BigInteger(serialNumber), startDate,
                endDate, csrData.getSubject(), csrData.getPublicKey());
        X509CertificateHolder certHolder = addExtensionsAndBuildCertificate(certGen, cert, contentSigner, false);

        // Convert to X509 certificate
        X509Certificate newCertificate = convertToX509Certificate(certHolder);

        // Save keystore and serial number
        ks.setCertificateEntry(serialNumber, newCertificate);
        saveKeyStore(ks, nonCAKeystore, nonCAPassword);

        // Create certificate file
        File certificateFile = x509CertificateToPem(cert, commonName);

        // Send certificate on email address
        String email = csrData.getSubject().getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString();

        try {
            emailService.sendEmail(email, certificateFile);
        } catch (MessagingException ignored) {
        }

        csrRepository.delete(csr);

        return "CA Certificate for " + commonName + " successfully created";
    }

    public Object createCACertificate(CertAuthorityDTO certAuth) throws KeyStoreException, IOException,
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
        System.out.println(pass);
        ks.setEntry(serialNumber.toString(), privKeyEntry, new KeyStore.PasswordProtection(pass.toCharArray()));
        saveKeyStore(ks, keystorePath, keystorePassword);

        return serialNumber.toString();
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
            certDTOs.add(new CertAuthorityDTO(csr.getId(), new JcaPKCS10CertificationRequest(csr.getCsr()).getSubject()));
        }

        return certDTOs;
    }

    public ArrayList<CertificateDTO> getCertificates(boolean ca) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = ca ? loadKeyStore(keystorePath, keystorePassword) : loadKeyStore(nonCAKeystore, nonCAPassword);
        ArrayList<CertificateDTO> certificates = new ArrayList<>();

        Enumeration enumeration = ks.aliases();
        while (enumeration.hasMoreElements()) {
            String alias = (String) enumeration.nextElement();
            X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
            certificates.add(new CertificateDTO(certificate, alias));
        }

        return certificates;
    }

    public String revokeCertificate(String commonName) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NotExistingCertificateException, CertificateAlreadyRevokedException {
        Optional<RevokedCertificate> r = revokedCertificatesRepository.findByCommonName(commonName);
        if (r.isPresent()) {
            throw new CertificateAlreadyRevokedException("Certificate with common name " + commonName + " is already revoked");
        }

        // Load non CA keystore
        KeyStore nonCAKS = loadKeyStore(nonCAKeystore, nonCAPassword);

        // Load CA keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        X509Certificate certificate = (X509Certificate) nonCAKS.getCertificate(commonName);
        if (certificate == null) {
            certificate = (X509Certificate) ks.getCertificate(commonName);
            if (certificate == null) {
                throw new NotExistingCertificateException("Certificate with name" + commonName + " doesn't exist");
            }
        }

        saveRevokedCertificate(certificate, commonName);
        return "Certificate successfully revoked";
    }

    private void saveRevokedCertificate(X509Certificate certificate, String commonName) throws CertificateNotYetValidException, CertificateExpiredException {
        certificate.checkValidity();
        String id = certificate.getSerialNumber().toString();
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

    @PostConstruct
    void proba() throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, OperatorCreationException, PKCSException, DigitalSignatureInvalidException, CertificateException, CANotValidException, KeyStoreException, UnrecoverableEntryException, ValidCertificateAlreadyExistsException, CSRDoesNotExistException {
        CertAuthorityDTO cDTO = new CertAuthorityDTO(null, "America Chamber",
                "America DefenceFirst", "Washington corp.",
                "Washington", "Washington", "USA", "master.daca09@gmail.com");
        String a = null;
        try {
            a = createCACertificate(cDTO).toString();
            System.out.println(a);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<CertificateDTO> certs = getCertificates(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.initialize(2048, new SecureRandom());
        KeyPair keypair = keyGen.generateKeyPair();
        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal("CN=Ole Nordmann, OU=ACME, O=Sales, C=NO, L=Oslo, ST=Oslo, EmailAddress=master.daca09@gmail.com"), publicKey);
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(privateKey);
        PKCS10CertificationRequest csr = p10Builder.build(signer);

        submitCSR(csr.getEncoded());
        ArrayList<CertAuthorityDTO> caDTO = getCertificateSigningRequests();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pwdArray = nonCAPassword.toCharArray();
        ks.load(null, pwdArray);
        saveKeyStore(ks, nonCAKeystore, nonCAPassword);
        System.out.println(createNonCACertificate(caDTO.get(0).getSerialNumber(),
                caDTO.get(0).getCommonName(), a));

    }
}
