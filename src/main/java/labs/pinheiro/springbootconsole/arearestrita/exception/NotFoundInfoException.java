package labs.pinheiro.springbootconsole.arearestrita.exception;


public class NotFoundInfoException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public NotFoundInfoException(String message) {
        super(message);

    }
}
