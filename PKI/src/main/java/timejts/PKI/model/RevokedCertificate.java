package timejts.PKI.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class RevokedCertificate {

    @Id
    private String id;
    private String commonName;
    private Date startDate;
    private Date endDate;
    private String issuer;

    public RevokedCertificate() {
        super();
    }

    public RevokedCertificate(String id, String commonName, Date startDate, Date endDate, String issuer) {
        this.id = id;
        this.commonName = commonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuer = issuer;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
