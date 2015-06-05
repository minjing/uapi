package uapi.service;

public interface IServiceGenerator<T> {

    T createService(Object serveFor);
}
