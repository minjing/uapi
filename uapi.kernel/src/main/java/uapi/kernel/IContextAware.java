package uapi.kernel;

public interface IContextAware extends IService {

    void setContext(IContext context);
}
