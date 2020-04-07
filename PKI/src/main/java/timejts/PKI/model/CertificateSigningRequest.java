package timejts.PKI.model;

import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;

public class CertificateSigningRequest {

    @Id
    private BigInteger id;
    private JcaPKCS10CertificationRequest csr;

    public CertificateSigningRequest(BigInteger id, JcaPKCS10CertificationRequest csr) {
        this.id = id;
        this.csr = csr;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public JcaPKCS10CertificationRequest getCsr() {
        return csr;
    }

    public void setCsr(JcaPKCS10CertificationRequest csr) {
        this.csr = csr;
    }
}
