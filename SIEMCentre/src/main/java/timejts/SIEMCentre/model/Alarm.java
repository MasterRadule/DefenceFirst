package timejts.SIEMCentre.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "alarms")
public class Alarm {

    @Id
    private BigInteger id;

    private int count;

    private Long timespan;

    private String sourceIPRegex;

    private Severity severity;

    private Facility facility;

    private String messageRegex;

    public Alarm() {
    }

    public Alarm(BigInteger id, int count, Long timespan,
                 String sourceIPRegex, Severity severity, Facility facility, String messageRegex) {
        this.id = id;
        this.count = count;
        this.timespan = timespan;
        this.sourceIPRegex = sourceIPRegex;
        this.severity = severity;
        this.facility = facility;
        this.messageRegex = messageRegex;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public String getMessageRegex() {
        return messageRegex;
    }

    public void setMessageRegex(String messageRegex) {
        this.messageRegex = messageRegex;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
