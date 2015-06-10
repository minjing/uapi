package uapi.service;

public interface IAnnotationMethodHandler<T> {

    void parse(AnnotatedMethod serviceMethod);
}
