package uapi.internal;

public interface IAnnotationParser<T> {

    void parse(T annotation, ServiceReference serviceReference);
}
