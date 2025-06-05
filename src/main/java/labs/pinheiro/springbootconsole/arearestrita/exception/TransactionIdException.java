package labs.pinheiro.springbootconsole.arearestrita.exception;

public class TransactionIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransactionIdException(String message, Exception e) {
        super(message, e);
    }

    public TransactionIdException(String message) {
        super(message);
    }
}
