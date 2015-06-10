package uapi.internal;

public interface IAnnotationHandler<T> {

    void parse(AnnotationServiceMethod serviceMethod);
}
