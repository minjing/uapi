/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.flow;

/**
 * The IHandler present that it can handle input data and
 * generate an output data.
 * 
 * @author min
 *
 * @param <I> The input data type
 * @param <O> The output data type
 */
public interface IHandler<I, O> {

    /**
     * The handler name.
     * 
     * @return The handler name
     */
    String getName();

    O handle(I input, IContext context);
}
