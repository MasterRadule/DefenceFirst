package timejts.PKI.exceptions;

public class CertificateAlreadyRevokedException extends  Exception{
    public CertificateAlreadyRevokedException(String errorMessage) {
        super(errorMessage);
    }
}
