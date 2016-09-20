/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi;

/**
 * The exception indicate the input argument is invalid
 */
public class InvalidArgumentException extends KernelException {

    private static final long serialVersionUID = -1108366334118915560L;

    public InvalidArgumentException(String argumentName, InvalidArgumentType type) {
        this("The argument is invalid - {}, cause - {}", argumentName, type.name());
    }

    public InvalidArgumentException(String message, Object... args) {
        super(message, args);
    }

    public enum InvalidArgumentType {

        EMPTY, FORMAT
    }
}
