/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi;

public class InvalidStateException extends KernelException {

    private static final long serialVersionUID = -1108366334118915560L;

    public InvalidStateException(String currentState, String expectedState) {
        super("Crrent state is {}, Expected state is {}", currentState, expectedState);
    }
}
