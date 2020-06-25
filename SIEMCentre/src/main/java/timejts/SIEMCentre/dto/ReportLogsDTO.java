package timejts.SIEMCentre.dto;

public class ReportLogsDTO {

    private String startDate;
    private String endDate;
    private String system;
    private String machine;

    public ReportLogsDTO() {
    }

    public ReportLogsDTO(String startDate, String endDate, String system, String machine) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.system = system;
        this.machine = machine;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }
}
