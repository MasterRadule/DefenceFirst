package timejts.PKI.dto;

import java.util.Date;

public class CreationDataDTO {

    private String sigAlgorithm;
    private Date startDate;
    private Date endDate;
    private boolean altNames;

    public CreationDataDTO(String sigAlgorithm, Date startDate, Date endDate, boolean altNames) {
        this.sigAlgorithm = sigAlgorithm;
        this.startDate = startDate;
        this.endDate = endDate;
        this.altNames = altNames;
    }

    public String getSigAlgorithm() {
        return sigAlgorithm;
    }

    public void setSigAlgorithm(String sigAlgorithm) {
        this.sigAlgorithm = sigAlgorithm;
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


    public boolean isAltNames() {
        return altNames;
    }

    public void setAltNames(boolean altNames) {
        this.altNames = altNames;
    }
}
