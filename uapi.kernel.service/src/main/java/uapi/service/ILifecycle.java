package uapi.service;

/**
 * A lifecycle management for service
 */
public interface ILifecycle {

    boolean onStateChange(int start, int end, Object service);
}
