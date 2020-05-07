package timejts.PKI.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CACertificateCreationDTO {

    @Valid
    @NotNull(message = "CA data must be provided")
    private SubjectDTO certAuthData;

    @Valid
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
