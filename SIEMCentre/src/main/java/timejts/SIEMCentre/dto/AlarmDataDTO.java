package timejts.SIEMCentre.dto;

import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Severity;

public class AlarmDataDTO {

    private int templateNumber;

    private int count;

    private Long timespan;

    private String sourceIPRegex;

    private Severity severityParam;

    private Facility facilityParam;

    private String messageRegex1;

    private String messageRegex2;

    public AlarmDataDTO() {}

    public AlarmDataDTO(int templateNumber, Alarm alarm) {
        this.templateNumber = templateNumber;
        this.count = alarm.getCount();
        this.timespan = alarm.getTimespan();
        this.sourceIPRegex = alarm.getSourceIPRegex();
        this.severityParam = alarm.getSeverity();
        this.facilityParam = alarm.getFacility();
        this.messageRegex1 = alarm.getMessageRegex1();
        this.messageRegex2 = alarm.getMessageRegex2();
    }

    public int getTemplateNumber() {
        return templateNumber;
    }

    public void setTemplateNumber(int templateNumber) {
        this.templateNumber = templateNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getTimespan() {
        return timespan;
    }

    public void setTimespan(Long timespan) {
        this.timespan = timespan;
    }

    public String getSourceIPRegex() {
        return sourceIPRegex;
    }

    public void setSourceIPRegex(String sourceIPRegex) {
        this.sourceIPRegex = sourceIPRegex;
    }

    public Severity getSeverityParam() {
        return severityParam;
    }

    public void setSeverityParam(Severity severityParam) {
        this.severityParam = severityParam;
    }

    public Facility getFacilityParam() {
        return facilityParam;
    }

    public void setFacilityParam(Facility facilityParam) {
        this.facilityParam = facilityParam;
    }

    public String getMessageRegex1() {
        return messageRegex1;
    }

    public void setMessageRegex1(String messageRegex1) {
        this.messageRegex1 = messageRegex1;
    }

    public String getMessageRegex2() {
        return messageRegex2;
    }

    public void setMessageRegex2(String messageRegex2) {
        this.messageRegex2 = messageRegex2;
    }
}
