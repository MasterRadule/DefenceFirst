package timejts.PKI.dto;

import timejts.PKI.model.RevokedCertificate;

import java.util.Date;

public class CertificateDTO {

    private String serialNumber;
    private String commonName;
    private Date startDate;
    private Date endDate;
    private String issuer;
    private boolean ca;

    public CertificateDTO(String serialNumber, String commonName, Date startDate, Date endDate, String issuer, boolean ca) {
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuer = issuer;
        this.ca = ca;
    }

    public CertificateDTO(RevokedCertificate r) {
        this.serialNumber = r.getId();
        this.commonName = r.getCommonName();
        this.startDate = r.getStartDate();
        this.endDate = r.getEndDate();
        this.issuer = r.getIssuer();
        this.ca = false;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public boolean isCa() {
        return ca;
    }

    public void setCa(boolean ca) {
        this.ca = ca;
    }
}
