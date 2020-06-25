package timejts.SIEMCentre.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

@Document(collection = "raisedAlarms")
public class RaisedAlarm {

    @Id
    private BigInteger id;

    private Date time;

    private AlarmType alarmType;

    private String sourceIP;

    private Severity severity;

    private Facility facility;

    private String message1;

    private String message2;

    private boolean raised;

    public RaisedAlarm() {
        raised = false;
    }

    public RaisedAlarm(BigInteger id, Date time, AlarmType alarmType, String sourceIP, Severity severity, Facility facility) {
        this.id = id;
        this.time = time;
        this.alarmType = alarmType;
        this.sourceIP = sourceIP;
        this.severity = severity;
        this.facility = facility;
        this.raised = false;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public boolean isRaised() {
        return raised;
    }

    public void setRaised(boolean raised) {
        this.raised = raised;
    }
}
