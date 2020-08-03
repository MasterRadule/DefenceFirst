package timejts.PKI.exceptions;

public class InvalidDigitalSignatureException extends Exception {

    public InvalidDigitalSignatureException(String errorMessage) {
        super(errorMessage);
    }
}
