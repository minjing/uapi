package uapi.service;

public interface IContext {

    IService getService(Class<? extends IService> type);
}
