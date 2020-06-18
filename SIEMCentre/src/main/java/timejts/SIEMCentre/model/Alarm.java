package timejts.SIEMCentre.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "alarms")
public class Alarm {

    @Id
    private BigInteger id;

    private int count;

    private int timespan;

    private String sourceIPRegex;

    private Severity severity;

    private Facility facility;

    private String messageRegex1;

    private String messageRegex2;

    public Alarm() {
    }

    public Alarm(BigInteger id, int count, int timespan, String sourceIPRegex, Severity severity, Facility facility, String messageRegex1, String messageRegex2) {
        this.id = id;
        this.count = count;
        this.timespan = timespan;
        this.sourceIPRegex = sourceIPRegex;
        this.severity = severity;
        this.facility = facility;
        this.messageRegex1 = messageRegex1;
        this.messageRegex2 = messageRegex2;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getTimespan() {
        return timespan;
    }

    public void setTimespan(int timespan) {
        this.timespan = timespan;
    }

    public String getSourceIPRegex() {
        return sourceIPRegex;
    }

    public void setSourceIPRegex(String sourceIPRegex) {
        this.sourceIPRegex = sourceIPRegex;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
