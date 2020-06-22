package timejts.SIEMCentre.dto;

import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Severity;

import java.util.Date;

public class SearchLogsDTO {

    private String messageRegex;
    private String hostname;
    private String hostIPRegex;
    private Date startDate;
    private Date endDate;
    private Severity severity;
    private Facility facility;

    public SearchLogsDTO() {}

    public SearchLogsDTO(String messageRegex, String hostname, String hostip, Date startDate, Date endDate,
                            Severity severity, Facility facility) {
        super();
        this.messageRegex = messageRegex;
        this.hostname = hostname;
        this.hostIPRegex = hostip;
        this.startDate = startDate;
        this.endDate = endDate;
        this.severity = severity;
        this.facility = facility;
    }

    public String getMessageRegex() {
        return messageRegex;
    }

    public void setMessageRegex(String messageRegex) {
        this.messageRegex = messageRegex;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostIPRegex() {
        return hostIPRegex;
    }

    public void setHostIPRegex(String hostIPRegex) {
        this.hostIPRegex = hostIPRegex;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
