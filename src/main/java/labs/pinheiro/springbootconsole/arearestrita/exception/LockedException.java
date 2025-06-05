package labs.pinheiro.springbootconsole.arearestrita.exception;


public class LockedException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public LockedException(String message) {
        super(message);

    }
}
