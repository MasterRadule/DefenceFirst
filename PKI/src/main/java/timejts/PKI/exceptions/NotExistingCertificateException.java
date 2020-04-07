package timejts.PKI.exceptions;

public class NotExistingCertificateException extends Exception {
    public NotExistingCertificateException(String errorMessage) {
        super(errorMessage);
    }
}
