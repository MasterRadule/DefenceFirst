package timejts.SIEMCentre.model;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;


@Document(collection = "logs")
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("10m")
public class Log {

    @Id
    private BigInteger id;

    @NotNull
    @Indexed
    private Date timestamp;

    @NotBlank
    @Indexed
    private String hostIP;

    @NotBlank
    private String sourceIP;

    @NotNull
    @Indexed
    private Severity severity;

    @NotNull
    @Indexed
    private Facility facility;

    @NotBlank
    @Indexed
    private String system;

    @NotBlank
    @Indexed
    private String hostname;

    private String message;

    public Log() {
    }

    public Log(Date timestamp, String hostname, String hostIP, String sourceIP, Severity severity, Facility facility, String message,
               String system) {
        super();
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.hostIP = hostIP;
        this.sourceIP = sourceIP;
        this.severity = severity;
        this.facility = facility;
        this.message = message;
        this.system = system;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
