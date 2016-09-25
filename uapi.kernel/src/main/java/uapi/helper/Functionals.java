/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

/**
 * Useful functional interface is defined here
 */
public interface Functionals {

    @FunctionalInterface
    interface Extractor<I, O, T extends Throwable> {
        O accept(I instance) throws T;
    }

    @FunctionalInterface
    interface Creator<T> {
        T accept();
    }

    @FunctionalInterface
    interface Convert<I, O> {
        O accept(I in);
    }

    @FunctionalInterface
    interface Action<I> {
        void accept(I in);
    }

    @FunctionalInterface
    interface Filter<T> {
        boolean accept(T in);
    }
}
