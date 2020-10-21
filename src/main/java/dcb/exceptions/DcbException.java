package dcb.exceptions;

@SuppressWarnings("CheckedExceptionClass")
public class DcbException extends Exception {
    private static final long serialVersionUID = 2007646151158399044L;

    public DcbException() {
    }

    public DcbException(String message) {
        super(message);
    }
}
