package timejts.PKI.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import timejts.PKI.dto.*;
import timejts.PKI.exceptions.CertificateRevokedException;
import timejts.PKI.exceptions.*;
import timejts.PKI.model.CertificateSigningRequest;
import timejts.PKI.model.RevokedCertificate;
import timejts.PKI.repository.CertificateSigningRequestRepository;
import timejts.PKI.repository.RevokedCertificatesRepository;
import timejts.SIEMCentre.dto.SignedLogsDTO;
import timejts.SIEMCentre.model.Log;

import javax.mail.MessagingException;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static timejts.PKI.utils.Utilities.*;

@Service
public class CertificateService {

    @Value("${server.ssl.key-store}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.trust-store}")
    private String truststorePath;

    @Value("${server.ssl.trust-store-password}")
    private String truststorePassword;

    @Value("${server.ssl.key-alias}")
    private String root;

    @Value("${spring.mail.username}")
    private String rootEmail;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RevokedCertificatesRepository revokedCertificatesRepository;

    @Autowired
    private CertificateSigningRequestRepository csrRepository;

    public String createNonCACertificate(NonCACertificateCreationDTO creationDTO) throws IOException, CertificateException,
            NoSuchAlgorithmException, CANotValidException, InvalidKeyException, UnrecoverableEntryException,
            CSRDoesNotExistException, CACertificateDoesNotExistException, ValidCertificateAlreadyExistsException,
            OperatorCreationException, KeyStoreException, InvalidCertificateDateException {

        Pair<CreationDataDTO, Boolean> data = processData(creationDTO.getCreationData());

        return createNonCACertificate(creationDTO.getSerialNumber(), creationDTO
                .getCaSerialNumber(), data.getFirst(), data.getSecond());
    }

    public String createCACertificate(CACertificateCreationDTO creationDTO) throws IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableEntryException, OperatorCreationException, KeyStoreException,
            InvalidCertificateDateException {

        Pair<CreationDataDTO, Boolean> data = processData(creationDTO.getCreationData());

        return createCACertificate(creationDTO.getCertAuthData(), data.getFirst(), data.getSecond());
    }

    private String createNonCACertificate(String serialNumber, String caSerialNumber, CreationDataDTO creationData,
                                          boolean defaultExtensions) throws KeyStoreException, IOException,
            CertificateException, CANotValidException, UnrecoverableEntryException, NoSuchAlgorithmException,
            OperatorCreationException, ValidCertificateAlreadyExistsException, CSRDoesNotExistException,
            InvalidKeyException, CACertificateDoesNotExistException, InvalidCertificateDateException {

        // Load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        // Check if subject already has valid certificate
        X509Certificate certificate = (X509Certificate) ks.getCertificate(serialNumber);
        if (certificate != null) {
            throw new ValidCertificateAlreadyExistsException("Certificate with given serial number already exists");
        }

        // Get CA certificate and private key and check validity of CA certificate
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder(creationData.getSigAlgorithm());
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
        Date endDate;
        if (creationData.getEndDate() == null) {
            LocalDateTime endLocalDate = certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime().minusMonths(3);
            endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            endDate = creationData.getEndDate();
            checkCertificateDates(creationData.getStartDate(), endDate, certificate.getNotAfter());
        }

        // Create Subject X500Name
        X500Name subject = createSubjectX500Name(csrData.getSubject(), certificate.getSerialNumber().toString());
        String email = csrData.getSubject().getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString();
        String issuerEmail = issuerName.getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString();

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, new BigInteger(serialNumber),
                creationData.getStartDate(), endDate, subject, csrData.getPublicKey());
        addBasicExtensions(certGen, false);
        if (!defaultExtensions) {
            if (creationData.isAltNames()) {
                addAlternativeNamesExtensions(certGen, email, issuerEmail);
            } else {
                addKeyIdentifierExtensions(certGen, csrData.getPublicKey(), certificate.getPublicKey());
            }
        }
        X509CertificateHolder certHolder = certGen.build(contentSigner);

        // Convert to X509 certificate
        X509Certificate newCertificate = convertToX509Certificate(certHolder);

        // Save keystore and serial number
        ks.setCertificateEntry(serialNumber, newCertificate);
        saveKeyStore(ks, keystorePath, keystorePassword);

        // Save certificate in truststore
        KeyStore truststore = loadKeyStore(truststorePath, truststorePassword);
        truststore.setCertificateEntry(serialNumber, newCertificate);
        saveKeyStore(truststore, truststorePath, truststorePassword);

        // Create certificate file
        File certificateFile = x509CertificateToPem(newCertificate, "", serialNumber);

        // Get CA certificates files
        File caZipped = getCACertificatesFiles();

        // Send certificate on email address
        try {
            emailService.sendEmailWithCertificateAndCAs(email, certificateFile, caZipped);
        } catch (MessagingException ignored) {
            ignored.printStackTrace();
        }

        csrRepository.delete(csr);
        certificateFile.delete();
        caZipped.delete();

        return "Certificate for " + getCommonName(newCertificate) + " successfully created";
    }

    private String createCACertificate(SubjectDTO certAuth, CreationDataDTO creationData, boolean defaultExtensions)
            throws KeyStoreException, IOException,
            UnrecoverableKeyException, NoSuchAlgorithmException, OperatorCreationException, CertificateException,
            InvalidCertificateDateException {

        // Get data about CA
        X500Name subjectCA = generateX500Name(certAuth);

        // Load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        // Get root certificate and private key
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder(creationData.getSigAlgorithm());
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        builder = builder.setProvider(bcp);
        X509Certificate cert = (X509Certificate) ks.getCertificate(root);
        PrivateKey privKey = (PrivateKey) ks.getKey(root, keystorePassword.toCharArray());

        // Build content signer and get issuer (root)
        ContentSigner contentSigner = builder.build(privKey);
        X500Name issuerName = new JcaX509CertificateHolder(cert).getSubject();

        // Generate CA public and private key
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Generate serial number and set start/end date
        BigInteger serialNumber = getSerialNumber();
        Date endDate;
        if (creationData.getEndDate() == null) {
            LocalDateTime endLocalDate = creationData.getStartDate().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime().plusYears(2).plusMonths(6);
            endDate = Date.from(endLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            endDate = creationData.getEndDate();
            checkCertificateDates(creationData.getStartDate(), endDate, cert.getNotAfter());
        }

        // Set certificate extensions and generate certificate
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName, serialNumber, creationData
                .getStartDate(), endDate, subjectCA, kp.getPublic());
        addBasicExtensions(certGen, true);
        if (!defaultExtensions) {
            if (creationData.isAltNames()) {
                addAlternativeNamesExtensions(certGen, certAuth.getEmail(), rootEmail);
            } else {
                addKeyIdentifierExtensions(certGen, kp.getPublic(), cert.getPublicKey());
            }
        }
        X509CertificateHolder certHolder = certGen.build(contentSigner);

        // Convert to X509 certificate
        X509Certificate newCertificate = convertToX509Certificate(certHolder);

        // Save certificate and private key in keystore and save serial number
        KeyStore.PrivateKeyEntry privKeyEntry = new KeyStore.PrivateKeyEntry(kp.getPrivate(),
                new Certificate[]{newCertificate});
        String pass = keystorePassword + serialNumber;
        ks.setEntry(serialNumber.toString(), privKeyEntry, new KeyStore.PasswordProtection(pass.toCharArray()));
        saveKeyStore(ks, keystorePath, keystorePassword);

        // Create certificate file
        x509CertificateToPem(newCertificate, "../PKI/src/main/resources/static/certAuths/", getCommonName(newCertificate));

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

        return "Certificate signing request successfully submitted. " +
                "Your certificate will be sent to your email address.";
    }

    public ArrayList<SubjectDTO> getCertificateSigningRequests() throws IOException {
        ArrayList<CertificateSigningRequest> csrs = (ArrayList<CertificateSigningRequest>) csrRepository.findAll();
        ArrayList<SubjectDTO> certDTOs = new ArrayList<>();
        for (CertificateSigningRequest csr : csrs) {
            certDTOs.add(new SubjectDTO(csr.getId().toString(), new JcaPKCS10CertificationRequest(csr.getCsr())
                    .getSubject(), root, rootEmail));
        }

        return certDTOs;
    }

    public SubjectDTO getCertificateSigningRequest(String serialNumber) throws CSRDoesNotExistException, IOException {
        CertificateSigningRequest csr = csrRepository.findById(new BigInteger(serialNumber))
                .orElseThrow(() -> new CSRDoesNotExistException("Certificate signing request " +
                        "with given serial number does not exist"));

        return new SubjectDTO(csr.getId().toString(), new JcaPKCS10CertificationRequest(csr.getCsr())
                .getSubject(), root, rootEmail);
    }

    public ArrayList<CertificateDTO> getCertificates(boolean ca) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);
        List<String> revokedSerNums = revokedCertificatesRepository.findAll().stream().map(RevokedCertificate::getId)
                .collect(Collectors.toList());
        ArrayList<CertificateDTO> certificates = new ArrayList<>();
        String alias, commonName, issuer;
        X509Certificate certificate;

        Enumeration<String> enumeration = ks.aliases();
        while (enumeration.hasMoreElements()) {
            alias = enumeration.nextElement();
            if (!revokedSerNums.contains(alias) && (!ca || ks.getCertificateChain(alias) != null)) {
                certificate = (X509Certificate) ks.getCertificate(alias);
                commonName = getCommonName(certificate);
                issuer = getIssuerCommonName(certificate);
                certificates.add(new CertificateDTO(alias, commonName, certificate.getNotBefore(), certificate
                        .getNotAfter(), issuer, ks.getCertificateChain(alias) != null));
            }
        }

        return certificates;
    }

    public CertificateDetailsDTO getCertificate(String serialNumber) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NotExistingCertificateException {
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);
        X509Certificate certificate = (X509Certificate) ks.getCertificate(serialNumber);
        if (certificate == null)
            throw new NotExistingCertificateException("Certificate with serial number" + serialNumber + " doesn't exist");

        SubjectDTO subjData = new SubjectDTO(serialNumber, new JcaX509CertificateHolder(certificate)
                .getSubject(), root, rootEmail);

        return new CertificateDetailsDTO(subjData, certificate.getNotBefore(), certificate
                .getNotAfter(), getIssuerCommonName(certificate), ks.getCertificateChain(serialNumber) != null);
    }

    public String revokeCertificate(String serialNumber) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NotExistingCertificateException, CertificateAlreadyRevokedException, InvalidRevocationException {
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
        if (ks.getCertificateChain(serialNumber) != null) {
            throw new InvalidRevocationException("Root and CA certificates can not be revoked");
        }

        saveRevokedCertificate(certificate);
        ks.deleteEntry(serialNumber);
        saveKeyStore(ks, keystorePath, keystorePassword);

        KeyStore truststore = loadKeyStore(truststorePath, truststorePassword);
        truststore.deleteEntry(serialNumber);
        saveKeyStore(truststore, truststorePath, truststorePassword);

        return "Certificate successfully revoked";
    }

    public String rejectCSR(String serialNumber) throws CSRDoesNotExistException {
        CertificateSigningRequest csr = csrRepository.findById(new BigInteger(serialNumber))
                .orElseThrow(() -> new CSRDoesNotExistException("Certificate signing request " +
                        "with given serial number does not exist"));
        csrRepository.delete(csr);

        return "Certificate signing request successfully rejected";
    }

    private void saveRevokedCertificate(X509Certificate certificate) throws CertificateNotYetValidException, CertificateExpiredException, CertificateEncodingException {
        String id = certificate.getSerialNumber().toString();
        String commonName = getCommonName(certificate);
        Date startDate = certificate.getNotBefore();
        Date endDate = certificate.getNotAfter();
        String issuer = getIssuerCommonName(certificate);
        RevokedCertificate certificateToBeRevoked = new RevokedCertificate(id, commonName, startDate, endDate, issuer);
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

    public boolean validateCertificate(byte[] certificateEncoded) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NotExistingCertificateException, CertificateRevokedException, CorruptedCertificateException, InvalidKeyException, NoSuchProviderException, SignatureException {

        // Decode certificate
        ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateEncoded);

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(inputStream);

        //load keystore
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        //check if certificate exists in keystore
        X509Certificate certificateFromKS = (X509Certificate) ks
                .getCertificate(certificate.getSerialNumber().toString());
        if (certificateFromKS == null) {
            throw new NotExistingCertificateException("Certificate doesn't exist");
        }

        //check if certificate from database is equal with given certificate
        if (!certificateFromKS.equals(certificate)) {
            throw new CorruptedCertificateException("Certificate is corrupted");
        }

        //check certificate revoke status
        if (checkCertificateStatus(certificate.getSerialNumber().toString(), ks).equals("Certificate is revoked")) {
            throw new CertificateRevokedException("Certificate is revoked");
        }

        //chain root -> c (dates, revoke status and key validation)
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        String caSerialNumber = x500name.getRDNs(BCStyle.SERIALNUMBER)[0].getFirst().getValue().toString();
        Certificate[] certificateChain = ks.getCertificateChain(caSerialNumber);
        X509Certificate child, parent;

        for (int i = certificateChain.length - 2; i >= 0; --i) {
            child = (X509Certificate) certificateChain[i];
            parent = (X509Certificate) certificateChain[i + 1];

            child.checkValidity();
            child.verify(parent.getPublicKey());
            if (checkCertificateStatus(child.getSerialNumber().toString(), ks).equals("Certificate is revoked")) {
                throw new CertificateRevokedException("Certificate is revoked");
            }
        }

        //checking date and key validation for given certificate(final result)
        X509Certificate caCertificate = (X509Certificate) ks.getCertificate(caSerialNumber);
        certificate.verify(caCertificate.getPublicKey());
        certificate.checkValidity();

        return true;
    }

    public String checkCertificateStatus(String serialNumber, KeyStore ks) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        if (ks == null) {
            ks = loadKeyStore(keystorePath, keystorePassword);
            X509Certificate cert = (X509Certificate) ks.getCertificate(serialNumber);
            if (cert == null) {
                return "Unknown certificate";
            }
        }

        Optional<RevokedCertificate> r = revokedCertificatesRepository
                .findById(serialNumber);
        if (r.isPresent()) {
            return "Certificate is revoked";
        }

        return "Certificate is good";
    }

    public List<CertificateDTO> getRevokedCertificates() {
        return revokedCertificatesRepository.findAll().stream().map(r -> new CertificateDTO(r))
                .collect(Collectors.toList());
    }

    public Boolean checkSignature(SignedLogsDTO signedLogsDTO, String serNum) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NotExistingCertificateException, SignatureException, InvalidKeyException {
        KeyStore ks = loadKeyStore(keystorePath, keystorePassword);

        X509Certificate certificate = (X509Certificate) ks.getCertificate(serNum);
        if (certificate == null) {
            throw new NotExistingCertificateException("Certificate with given serial number does not exist");
        }

        PublicKey publicKey = certificate.getPublicKey();
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Log>>() {
        }.getType();
        String json = gson.toJson(signedLogsDTO.getLogs(), type);
        byte[] bytes = json.getBytes();

        signature.update(bytes);

        return signature.verify(signedLogsDTO.getSignedLogs());
    }
}
