package uapi.service;

import uapi.KernelException;
import uapi.rx.Looper;
import uapi.service.internal.IServiceHolder;

import java.util.Stack;

/**
 * The exception represent some services have dependency cycle
 */
public class CycleDependencyException extends KernelException {

    private final Stack<IServiceHolder> _dependencyStack;

    public CycleDependencyException(
            final Stack<IServiceHolder> dependencyStack
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
