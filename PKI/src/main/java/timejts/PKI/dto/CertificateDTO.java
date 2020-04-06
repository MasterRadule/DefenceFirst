package timejts.PKI.dto;

import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateDTO {

    private String commonName;
    private Date startDate;
    private Date endDate;
    private String ca;

    public CertificateDTO(String commonName, Date startDate, Date endDate, String ca) {
        this.commonName = commonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ca = ca;
    }

    public CertificateDTO(X509Certificate certificate, String alias) {
        this.commonName = alias;
        this.startDate = certificate.getNotBefore();
        this.endDate = certificate.getNotAfter();
        this.ca = certificate.getIssuerX500Principal().getName();
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

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }
}
