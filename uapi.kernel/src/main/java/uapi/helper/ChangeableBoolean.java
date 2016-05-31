/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

/**
 * A changeable boolean object
 */
public class ChangeableBoolean {

    private boolean _value;

    public ChangeableBoolean() {
        this(false);
    }

    public ChangeableBoolean(boolean value) {
        this._value = value;
    }

    public void set(boolean value) {
        this._value = value;
    }

    public boolean get() {
        return this._value;
    }
}
