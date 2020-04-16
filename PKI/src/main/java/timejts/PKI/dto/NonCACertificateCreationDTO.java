package timejts.PKI.dto;

public class NonCACertificateCreationDTO {

    private String serialNumber;
    private String caSerialNumber;
    private CreationDataDTO creationData;

    public NonCACertificateCreationDTO(String serialNumber, String caSerialNumber, CreationDataDTO creationData) {
        this.serialNumber = serialNumber;
        this.caSerialNumber = caSerialNumber;
        this.creationData = creationData;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCaSerialNumber() {
        return caSerialNumber;
    }

    public void setCaSerialNumber(String caSerialNumber) {
        this.caSerialNumber = caSerialNumber;
    }

    public CreationDataDTO getCreationData() {
        return creationData;
    }

    public void setCreationData(CreationDataDTO creationData) {
        this.creationData = creationData;
    }
}
