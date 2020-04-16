package timejts.PKI.dto;

public class CACertificateCreationDTO {

    private SubjectDTO certAuthData;
    private CreationDataDTO creationData;

    public CACertificateCreationDTO(SubjectDTO certAuthData, CreationDataDTO creationData) {
        this.certAuthData = certAuthData;
        this.creationData = creationData;
    }

    public SubjectDTO getCertAuthData() {
        return certAuthData;
    }

    public void setCertAuthData(SubjectDTO certAuthData) {
        this.certAuthData = certAuthData;
    }

    public CreationDataDTO getCreationData() {
        return creationData;
    }

    public void setCreationData(CreationDataDTO creationData) {
        this.creationData = creationData;
    }
}
