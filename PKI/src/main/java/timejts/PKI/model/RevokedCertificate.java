package timejts.PKI.model;

import org.springframework.data.annotation.Id;

public class RevokedCertificate {

    @Id
    private String id;
    private String commonName;

    public RevokedCertificate(){
        super();
    }

    public RevokedCertificate(String id, String commonName){
        this.id = id;
        this.commonName = commonName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
}
