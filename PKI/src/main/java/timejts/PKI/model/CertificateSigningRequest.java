package timejts.PKI.model;

import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "csr")
public class CertificateSigningRequest {

    @Id
    private Integer id;
    private JcaPKCS10CertificationRequest csr;

    public CertificateSigningRequest(Integer id, JcaPKCS10CertificationRequest csr) {
        this.id = id;
        this.csr = csr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JcaPKCS10CertificationRequest getCsr() {
        return csr;
    }

    public void setCsr(JcaPKCS10CertificationRequest csr) {
        this.csr = csr;
    }
}
