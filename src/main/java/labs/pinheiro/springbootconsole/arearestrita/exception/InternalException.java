package labs.pinheiro.springbootconsole.arearestrita.exception;


public class InternalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InternalException(String message, Exception e) {
        super(message, e);
    }

    public InternalException(String message) {
        super(message);
    }
}
