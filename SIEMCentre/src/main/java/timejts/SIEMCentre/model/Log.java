package timejts.SIEMCentre.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;


@Document(collection = "logs")
public class Log {

    @Id
    private BigInteger id;

    @Indexed
    private Date timestamp;

    @Indexed
    private String hostIP;

    @Indexed
    private Severity severity;

    @Indexed
    private Facility facility;

    @Indexed
    private String os;

    private String hostname;

    private String message;

    private String log;

    public Log() {
    }

    public Log(Date timestamp, String hostname, String hostIP, Severity severity, Facility facility, String message,
               String log, String os) {
        super();
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.hostIP = hostIP;
        this.severity = severity;
        this.facility = facility;
        this.message = message;
        this.log = log;
        this.os = os;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
