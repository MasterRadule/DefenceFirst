package timejts.PKI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "csr")
public class CertificateSigningRequest {

    @Id
    private BigInteger id;
    private byte[] csr;

    public CertificateSigningRequest(BigInteger id, byte[] csr) {
        this.id = id;
        this.csr = csr;
    }

    public byte[] getCsr() {
        return csr;
    }

    public void setCsr(byte[] csr) {
        this.csr = csr;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
