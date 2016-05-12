package uapi.helper;

/**
 * Useful functional interface is defined here
 */
public class Functionals {

    private Functionals() { }

    @FunctionalInterface
    public interface Extractor<I, O, T extends Throwable> {
        O accept(I instance) throws T;
    }

    @FunctionalInterface
    public interface Creator<T> {
        T accept();
    }
}
