package uapi.kernel;

public interface IContext {

    IService getService(Class<? extends IService> type);
}
