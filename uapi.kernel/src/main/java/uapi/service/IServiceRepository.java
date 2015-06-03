package uapi.service;

import uapi.internal.IAnnotationParser;

public interface IServiceRepository {

    void addAnnotationParser(IAnnotationParser<?> parser);
}
