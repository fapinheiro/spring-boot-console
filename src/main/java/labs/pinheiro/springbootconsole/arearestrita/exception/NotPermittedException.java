package labs.pinheiro.springbootconsole.arearestrita.exception;


public class NotPermittedException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public NotPermittedException(String message) {
        super(message);

    }
}
