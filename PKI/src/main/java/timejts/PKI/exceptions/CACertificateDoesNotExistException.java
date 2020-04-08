package timejts.PKI.exceptions;

public class CACertificateDoesNotExistException extends Exception {

    public CACertificateDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
