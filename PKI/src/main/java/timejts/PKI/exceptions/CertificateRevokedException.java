package timejts.PKI.exceptions;

public class CertificateRevokedException extends Exception {
    public CertificateRevokedException(String errorMessage) {
        super(errorMessage);
    }
}
