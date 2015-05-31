package uapi.service;

public interface IContextAware extends IService {

    void setContext(IContext context);
}
