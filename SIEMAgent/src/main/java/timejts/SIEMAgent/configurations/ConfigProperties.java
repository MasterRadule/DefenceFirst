package timejts.SIEMAgent.configurations;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@Component
@ConfigurationProperties("app")
@Validated
public class ConfigProperties {

    @Pattern(regexp = "^(Windows|Linux|Simulator)$", message = "Invalid agent mode")
    private String agentMode;

    private String regex;

    private String logName;

    private Boolean realTimeMode;

    private int batchTime;

    public String getAgentMode() {
        return agentMode;
    }

    public String getRegex() {
        return regex;
    }

    public String getLogName() {
        return logName;
    }

    public Boolean getRealTimeMode() {
        return realTimeMode;
    }

    public int getBatchTime() {
        return batchTime;
    }

    public void setAgentMode(String agentMode) {
        this.agentMode = agentMode;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public void setRealTimeMode(Boolean realTimeMode) {
        this.realTimeMode = realTimeMode;
    }

    public void setBatchTime(int batchTime) {
        this.batchTime = batchTime;
    }
}
