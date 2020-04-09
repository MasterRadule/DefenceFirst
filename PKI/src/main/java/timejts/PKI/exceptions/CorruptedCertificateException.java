package timejts.PKI.exceptions;

public class CorruptedCertificateException extends Exception {
    public CorruptedCertificateException(String errorMessage) {
        super(errorMessage);
    }
}
