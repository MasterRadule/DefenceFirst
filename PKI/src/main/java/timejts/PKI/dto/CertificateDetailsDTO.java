package timejts.PKI.dto;

import java.util.Date;

public class CertificateDetailsDTO {

    private SubjectDTO subjectData;
    private Date startDate;
    private Date endDate;
    private String issuer;
    private boolean ca;

    public CertificateDetailsDTO(SubjectDTO subjectData, Date startDate, Date endDate, String issuer, boolean ca) {
        this.subjectData = subjectData;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuer = issuer;
        this.ca = ca;
    }

    public SubjectDTO getSubjectData() {
        return subjectData;
    }

    public void setSubjectData(SubjectDTO subjectData) {
        this.subjectData = subjectData;
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

    public boolean isCa() {
        return ca;
    }

    public void setCa(boolean ca) {
        this.ca = ca;
    }
}
