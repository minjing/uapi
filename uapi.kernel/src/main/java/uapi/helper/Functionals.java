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
public class Functionals {

    private Functionals() { }

    @FunctionalInterface
    public interface Extractor<I, O, T extends Throwable> {
        O accept(I instance) throws T;
    }

    @FunctionalInterface
    public interface Creator<T> {
        T accept();
    }

    @FunctionalInterface
    public interface Convert<I, O> {
        O accept(I in);
    }

    @FunctionalInterface
    public interface Action<I> {
        void accept(I in);
    }

    @FunctionalInterface
    public interface Filter<T> {
        boolean accept(T in);
    }
}
