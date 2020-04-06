package timejts.PKI.exceptions;

public class DigitalSignatureInvalidException extends Exception {

    public DigitalSignatureInvalidException(String errorMessage) {
        super(errorMessage);
    }
}
