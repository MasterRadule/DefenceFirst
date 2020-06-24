package timejts.SIEMCentre.dto;

import timejts.SIEMCentre.model.Log;

import java.util.ArrayList;

public class SignedLogsDTO {

    private ArrayList<Log> logs;
    private byte[] signedLogs;

    public SignedLogsDTO() {}

    public SignedLogsDTO(ArrayList<Log> logs, byte[] signedLogs) {
        this.logs = logs;
        this.signedLogs = signedLogs;
    }

    public ArrayList<Log> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<Log> logs) {
        this.logs = logs;
    }

    public byte[] getSignedLogs() {
        return signedLogs;
    }

    public void setSignedLogs(byte[] signedLogs) {
        this.signedLogs = signedLogs;
    }
}
