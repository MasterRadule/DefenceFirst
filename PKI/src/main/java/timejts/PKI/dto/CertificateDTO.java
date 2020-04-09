package timejts.PKI.dto;

import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateDTO {

    private String serialNumber;
    private Date startDate;
    private Date endDate;
    private String issuerSerialNumber;
    private boolean ca;

    public CertificateDTO(String commonName, Date startDate, Date endDate, String issuerSerialNumber, boolean ca) {
        this.serialNumber = commonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuerSerialNumber = issuerSerialNumber;
        this.ca = ca;
    }

    public CertificateDTO(X509Certificate certificate, String alias, boolean ca) {
        this.serialNumber = alias;
        this.startDate = certificate.getNotBefore();
        this.endDate = certificate.getNotAfter();
        this.issuerSerialNumber = certificate.getSerialNumber().toString();
        this.ca = ca;
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

    public String getIssuerSerialNumber() {
        return issuerSerialNumber;
    }

    public void setIssuerSerialNumber(String issuerSerialNumber) {
        this.issuerSerialNumber = issuerSerialNumber;
    }

    public boolean isCa() {
        return ca;
    }

    public void setCa(boolean ca) {
        this.ca = ca;
    }
}
