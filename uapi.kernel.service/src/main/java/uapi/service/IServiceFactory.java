package uapi.service;

/**
 * Created by xquan on 3/9/2016.
 */
public interface IServiceFactory<T> {

    T createService(Object serveFor);
}
