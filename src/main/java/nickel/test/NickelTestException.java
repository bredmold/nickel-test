package nickel.test;

public class NickelTestException extends RuntimeException {
    public NickelTestException(Throwable t) {
        super(t);
    }

    public NickelTestException(String s) {
        super(s);
    }
}
