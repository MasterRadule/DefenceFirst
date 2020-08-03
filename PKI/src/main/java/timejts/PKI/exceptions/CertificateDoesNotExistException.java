package timejts.PKI.exceptions;

public class CertificateDoesNotExistException extends Exception {
    public CertificateDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
