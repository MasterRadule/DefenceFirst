package timejts.SIEMCentre.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "raisedAlarm")
public class RaisedAlarm {

    @Id
    private String id;

    private Date time;

    private AlarmType alarmType;

    private String sourceIP;

    private Severity severity;

    private Facility facility;

    private String message1;

    private String message2;

    public RaisedAlarm() {
    }

    public RaisedAlarm(String id, Date time, AlarmType alarmType, String sourceIP, Severity severity, Facility facility) {
        this.id = id;
        this.time = time;
        this.alarmType = alarmType;
        this.sourceIP = sourceIP;
        this.severity = severity;
        this.facility = facility;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
