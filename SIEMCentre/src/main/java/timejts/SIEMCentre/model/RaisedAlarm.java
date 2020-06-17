package timejts.SIEMCentre.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "raisedAlarm")
public class RaisedAlarm {

    @Id
    private String id;

    private Date time;

    private Log log;

    private Alarm alarm;

    public RaisedAlarm() {}

    public RaisedAlarm(String id, Date time, Log log, Alarm alarm) {
        this.id = id;
        this.time = time;
        this.log = log;
        this.alarm = alarm;
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

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
