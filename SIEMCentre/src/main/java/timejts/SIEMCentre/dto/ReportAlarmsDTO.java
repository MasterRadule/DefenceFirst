package timejts.SIEMCentre.dto;

import timejts.SIEMCentre.model.AlarmType;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Severity;

public class ReportAlarmsDTO {

    private String startDate;
    private String endDate;
    private Severity severity;
    private Facility facility;
    private AlarmType alarmType;

    public ReportAlarmsDTO() {
    }

    public ReportAlarmsDTO(String startDate, String endDate, Severity severity, Facility facility, AlarmType alarmType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.severity = severity;
        this.facility = facility;
        this.alarmType = alarmType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }
}
