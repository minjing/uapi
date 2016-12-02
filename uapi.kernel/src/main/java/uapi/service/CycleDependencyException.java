package uapi.service;

import uapi.KernelException;
import uapi.rx.Looper;
import uapi.service.internal.ServiceHolder2;

import java.util.Stack;

/**
 * The exception represent some services have dependency cycle
 */
public class CycleDependencyException extends KernelException {

    private final Stack<ServiceHolder2> _dependencyStack;

    public CycleDependencyException(
            final Stack<ServiceHolder2> dependencyStack
    ) {
        super("Found dependency cycle");
        this._dependencyStack = dependencyStack;
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuffer = new StringBuilder(super.getMessage());
        Looper.from(this._dependencyStack)
                .foreach(dependency -> {
                    msgBuffer.append(" -> ");
                    msgBuffer.append(dependency.getQualifiedId());
                });
        return msgBuffer.toString();
    }
}
