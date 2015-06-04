package uapi.helper;

@FunctionalInterface
public interface Resultful<T> {

    T run();
}
