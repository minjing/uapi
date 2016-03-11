package uapi.annotation.internal;

import rx.Observable;
import uapi.KernelException;
import uapi.annotation.AnnotationHandler;
import uapi.annotation.LogSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Used to order annotation
 */
final class AnnotationOrder {

    private final List<String> _ordered;
    private final List<AnnotationHandler> _unordered;
    private final LogSupport _logger;

    AnnotationOrder(LogSupport logger) {
        this._ordered = new ArrayList<>();
        this._unordered = new ArrayList<>();
        this._logger = logger;
    }

    void doOrder(final AnnotationHandler handler) {
        this._logger.info("--> do " + handler);
        Class[] annoClasses = handler.afterHandledAnnotations();
        if (annoClasses.length == 0) {
            this._ordered.add(handler.getSupportAnnotationType().getName());
            resolveUnordered(handler.getSupportAnnotationType().getName());
        } else {
            int maxIdx = -1;
            for (Class annoClass : annoClasses) {
                int idx = this._ordered.indexOf(annoClass.getName());
                if (idx < 0) {
                    this._unordered.add(handler);
                    return;
                } else {
                    if (maxIdx < idx) {
                        maxIdx = idx;
                    }
                }
                this._logger.info("idx --> " + idx + ", " + annoClass.getName());
            }
            if (maxIdx != -1) {
                this._logger.info("maxId --> " + maxIdx);
                if (maxIdx + 1 >= this._ordered.size()) {
                    this._ordered.add(handler.getSupportAnnotationType().getName());
                } else {
                    this._ordered.add(maxIdx + 1, handler.getSupportAnnotationType().getName());
                }
                resolveUnordered(handler.getSupportAnnotationType().getName());
            }
        }
        this._logger.info("after --> " + this._ordered);
    }

    private void resolveUnordered(String orderedAnnoName) {
        List<AnnotationHandler> dependHandlers = new ArrayList<>();
        Observable.from(this._unordered)
                .filter(handler -> {
                    for (Class annoClass : handler.afterHandledAnnotations()) {
                        if (annoClass.getName().equals(orderedAnnoName)) {
                            return true;
                        }
                    }
                    return false;
                }).subscribe(dependHandlers::add);

        for (AnnotationHandler handler : dependHandlers) {
            Class[] annoClasses = handler.afterHandledAnnotations();
            int maxIdx = -1;
            for (Class annoClass : annoClasses) {
                int idx = this._ordered.indexOf(annoClass.getName());
                if (idx < 0) {
                    return;
                } else {
                    if (maxIdx < idx) {
                        maxIdx = idx;
                    }
                }
            }
            if (maxIdx != -1) {
                this._unordered.remove(handler);
                if (maxIdx + 1 >= this._ordered.size()) {
                    this._ordered.add(handler.getSupportAnnotationType().getName());
                } else {
                    this._ordered.add(maxIdx, handler.getSupportAnnotationType().getName());
                }
                resolveUnordered(handler.getSupportAnnotationType().getName());
            }
        }
    }

    List<String> getOrderedAnnotations() {
        if (this._unordered.size() != 0) {
            throw new KernelException("There are some annotation can't be ordered - {}", this._unordered);
        }
        return this._ordered;
    }
}
